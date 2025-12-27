package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import web.mvc.santa_backend.chat.dto.*;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.manager.ChatroomManager;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.ChatroomRepository;
import web.mvc.santa_backend.chat.repository.MessageRepository;
import web.mvc.santa_backend.common.enumtype.MessageType;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.*;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ChatroomMemberServiceImpl implements ChatroomMemberService {
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    private final ChatroomManager chatroomManager;
    private final MessageService messageService;
    private final ChatroomService chatroomService;

    @Override
    @Transactional(readOnly = true)
    public List<ChatroomMemberResDTO> getChatroomMembers(Long chatroomId, boolean isBanned, Long userId) {
        //chatroomId로 현재유저/강퇴유저를 isBanned로 구별해서 불러오기
        List<ChatroomMembers> chatroomMembers =
                chatroomMemberRepository.findByChatroomAndIsBanned(Chatrooms.builder().chatroomId(chatroomId).build(), isBanned);

        List<ChatroomMemberResDTO> chatMemberList = new ArrayList<>();
        Map<Long, WebSocketSession> roomSessions = chatroomManager.getRoomSessions(chatroomId);
        //chatroomMember에 들어있는 user엔티티를... userSimpleDTO로 변환후 리스트에 add
        for (ChatroomMembers m : chatroomMembers) {
            boolean result = roomSessions.containsKey(m.getUser().getUserId());
            chatMemberList.add(
                    ChatroomMemberResDTO.builder()
                            .id(m.getUser().getUserId())
                            .username(m.getUser().getUsername())
                            .avatarUrl(m.getUser().getProfileImage())
                            .role(m.getRole())
                            .online(result)
                            .build()
            );
        }
        return chatMemberList;
    }

    @Override
    public void enterChatroom(ChatroomMemberDTO chatroomMemberDTO, WebSocketSession webSocketSession) {
        //Banned상태인지 확인
        boolean result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomMemberDTO.getChatroomId(), chatroomMemberDTO.getUserId(), true);
        if(result){
            //TODO 예외 만들기
            throw new RuntimeException("강퇴당한 유저는 재입장이 불가능합니다.");
        }

        log.info("chatroomId : {}", chatroomMemberDTO.getChatroomId());
        log.info("userId : {}", chatroomMemberDTO.getUserId());
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomMemberDTO.getChatroomId(), chatroomMemberDTO.getUserId()).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        //첫 참여자인지 확인
        if(!chatroomMember.isJoinNoticeSent()){
            //첫 참여자라면...
            //메모리로 관리되는 채팅방에 현재 접속자의 세션 추가
            chatroomManager.addSession(webSocketSession);
            //username을 가지고 오기 위해서 find
            Users user = userRepository.findById(chatroomMember.getUser().getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

            //username님 입장 이라는 메시지를 공지티압으로 db에 저장 후 out메시지 DTO로 받아오기
            InboundChatMessageDTO message = InboundChatMessageDTO.builder()
                    .userId(chatroomMember.getUser().getUserId())
                    .chatroomId(chatroomMember.getChatroom().getChatroomId())
                    .payload(user.getUsername() + "님 입장")
                    .type(MessageType.NOTICE)
                    .build();
            OutboundChatMessageDTO outMessage = messageService.createMessage(message);

            //현재 채팅방에 접속하고 있는 모든 사람에게 메시지 broadcast
            chatroomManager.broadcast(outMessage, chatroomMemberDTO.getChatroomId());
            updateNoticeSent(chatroomMember.getChatroomMemeberId());
        }else {
            //채팅방 입장
            chatroomManager.addSession(webSocketSession);
            //입장한 채팅방의 가장 최근 messageId를 가지고와서,
            Long latestMessageId = messageRepository.findLatestMessageId(chatroomMemberDTO.getChatroomId());
            //lastRead를 최신화 시킴.
            chatroomMemberDTO.setLastRead(latestMessageId);
            updateChatroomMember(chatroomMemberDTO.getUserId(), chatroomMemberDTO);
        }
    }


    @Override
    public ChatroomMemberResDTO createChatroomMember(ChatroomMemberDTO chatroomMemberDTO) {
        log.info("여기에 들어와야하는데..?");
        log.info("chatroomMemberDTO : {}", chatroomMemberDTO);
        //방이 있는지 확인
        Chatrooms chatroom = chatroomRepository.findById(chatroomMemberDTO.getChatroomId()).orElseThrow(() -> new ChatroomNotFoundException(ErrorCode.CHATROOM_NOT_FOUND));
        //유저가 실제로 있는지 확인
        Users user = userRepository.findById(chatroomMemberDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        //이 두개가 채팅멤버의 unique조건. 중복되면 안되기때문에 있는지 확인 후... 있으면 null 반환
        if(chatroomMemberRepository.existsByChatroomAndUser(chatroom,user)){
            return null;
        }
        //없는경우에는....
        //입장한 방의 가장 최신 messageId를 가지고 옴, null일 수 있지만 entity변환시 0으로 바꾸기 때문에 상관없음
        Long latestMessageId = messageRepository.findLatestMessageId(chatroomMemberDTO.getChatroomId());
        long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(chatroomMemberDTO.getChatroomId(), false);
        if(count == 0){
            chatroomMemberDTO.setRole(UserRole.ADMIN);
        }else { //참여자가 0명이 아니라면 기존 채팅방에 참여하는 사람 -> 즉 User
            chatroomMemberDTO.setRole(UserRole.USER);
        }
        //입장한 순간의 메시지id 기록(이 사람은 여기서부터 메시지를 읽을 수 있음. 입장 전의 메시지는 확인하지 못함)
        chatroomMemberDTO.setStartRead(latestMessageId);
        //입장한 순간 lastRead를 최신화
        chatroomMemberDTO.setLastRead(latestMessageId);
        //엔티티 변환
        ChatroomMembers chatroomMember = toEntity(chatroomMemberDTO);
        ChatroomMembers save = chatroomMemberRepository.save(chatroomMember);
        ChatroomMemberResDTO dto = toDTO(save, user);
        ReadUpdateDTO messageDTO = ReadUpdateDTO
                .builder()
                .messageType(MessageType.MEMBER_IN)
                .chatroomMemberDTO(dto)
                .online(true)
                .build();

        chatroomManager.broadcast(messageDTO, chatroomMemberDTO.getChatroomId());
        log.info("여기까지는 오는건가");
        return dto;
    }

    @Override
    public void updateChatroomMember(Long userId, ChatroomMemberDTO chatroomMemberDTO) {
        //같은 채팅방에 있는지 확인
        checkChatMember(chatroomMemberDTO.getChatroomId(), userId);

        //요청한 사람의 chatroomMember 레코드를 가져옴
        Users currentUser = Users.builder().userId(userId).build();
        Chatrooms currentChatroom = Chatrooms.builder().chatroomId(chatroomMemberDTO.getChatroomId()).build();
        ChatroomMembers currentChatMember = chatroomMemberRepository.findByUserAndChatroom(currentUser, currentChatroom).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));

        //관리할 상대의 chatroomMember 레코드를 가져옴
        Users user = Users.builder().userId(chatroomMemberDTO.getUserId()).build();
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByUserAndChatroom(user, currentChatroom).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));

        //최근 읽은 글 업데이트
        if(chatroomMemberDTO.getLastRead() != null){
            chatroomMember.setLastRead(chatroomMemberDTO.getLastRead());
        }
        //알림 on/off 설정
        if(chatroomMemberDTO.getNoteOff() != null){
            chatroomMember.setNoteOff(chatroomMemberDTO.getNoteOff());
        }
        //강제 퇴장(Admin만 가능)
        if(chatroomMemberDTO.getIsBanned() != null){
            if(currentChatMember.getRole().equals(UserRole.ADMIN)){
                chatroomMember.setBanned(chatroomMemberDTO.getIsBanned());
            }else{
                throw new ForbiddenException(ErrorCode.NOT_CHATROOM_ADMIN);
            }
        }
        //role 변경... 고민중..
        if(chatroomMemberDTO.getRole() != null && currentChatMember.getRole().equals(UserRole.ADMIN)){
            chatroomMember.setRole(chatroomMemberDTO.getRole());
        }
    }

    @Override
    public void deleteChatroomMember(Long userId, String username, Long chatroomId) {
        log.info("1단계");
        InboundChatMessageDTO message = InboundChatMessageDTO.builder()
                .userId(userId)
                .chatroomId(chatroomId)
                .payload(username + "님 퇴장")
                .type(MessageType.NOTICE)
                .build();
        OutboundChatMessageDTO outMessage = messageService.createMessage(message);
        log.info("2단계");
        chatroomManager.broadcast(outMessage, chatroomId);
        log.info("3단계");
        chatroomMemberRepository.deleteByUser_UserIdAndChatroom_ChatroomId(userId, chatroomId);
        long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(chatroomId, false);
        log.info("4단계");
        if(count == 0){
            chatroomService.deleteChatroom(chatroomId);
        }
    }

    @Override
    public boolean checkChatroomMember(Long userId, Long chatroomId) {
        return chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId, userId, false);
    }

    @Override
    public Long countChatroomMember(Long chatroomId) {
        return chatroomMemberRepository.countChatroomMembersByChatroom_ChatroomIdAndIsBanned(chatroomId, false);
    }

    @Override
    public void updateNoticeSent(Long chatroomMemberId) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findById(chatroomMemberId).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        chatroomMember.setJoinNoticeSent(true);
    }

    @Override
    public UserRole getUserRole(Long userId, Long chatroomId) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomId, userId).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        return UserRole.valueOf(chatroomMember.getRole().toString());
    }

    private ChatroomMembers toEntity(ChatroomMemberDTO chatroomMemberDTO) {
        Chatrooms chatroom = Chatrooms.builder().chatroomId(chatroomMemberDTO.getChatroomId()).build();
        Users user = Users.builder()
                .userId(chatroomMemberDTO.getUserId())
                .build();
        return ChatroomMembers.builder()
                .chatroom(chatroom)
                .user(user)
                .startRead(chatroomMemberDTO.getStartRead()!=null ? chatroomMemberDTO.getStartRead() : 0)
                .lastRead(chatroomMemberDTO.getLastRead()!=null ? chatroomMemberDTO.getLastRead() : 0)
                .noteOff(chatroomMemberDTO.getNoteOff()!=null ? chatroomMemberDTO.getNoteOff() : false)
                .role(chatroomMemberDTO.getRole()!=null ? chatroomMemberDTO.getRole() : UserRole.USER)
                .isBanned(chatroomMemberDTO.getIsBanned()!=null ? chatroomMemberDTO.getIsBanned() : false)
                .joinNoticeSent(false)
                .build();
    }

    private ChatroomMemberResDTO toDTO(ChatroomMembers chatroomMembers, Users user) {
        return ChatroomMemberResDTO
                .builder()
                .id(chatroomMembers.getChatroomMemeberId())
                .username(chatroomMembers.getUser().getUsername())
                .avatarUrl(user.getProfileImage())
                .role(chatroomMembers.getRole())
                .online(false)
                .build();
    }

    private void checkChatMember(Long chatroomId, Long userId){
        boolean result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId, userId, false);
        if(!result){
            throw new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER);
        }
    }
}
