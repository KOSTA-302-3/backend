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
    private final ChatroomManager chatroomManager;
    private final MessageService messageService;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatroomMemberResDTO> getChatroomMembers(Long chatroomId, boolean isBanned, Long userId) {
        //먼저 방 참여자인지의 검증
        checkChatMember(chatroomId, userId);
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

        //기존 참여자인지 확인
        result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomMemberDTO.getChatroomId(), chatroomMemberDTO.getUserId(), false);
        //기존 참여자가 아니라면 참여멤버 테이블에 레코드 추가
        if(!result){
            //참여자가 0명이라면 방을 만든사람 -> 즉 Admin
            long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(chatroomMemberDTO.getChatroomId(), false);
            if(count == 0){
                chatroomMemberDTO.setRole(UserRole.ADMIN);
            }else { //참여자가 0명이 아니라면 기존 채팅방에 참여하는 사람 -> 즉 User
                chatroomMemberDTO.setRole(UserRole.USER);
            }
            //참여멤버 테이블에 레코드 추가
            ChatroomMembers chatroomMember = createChatroomMember(chatroomMemberDTO);
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
        }else {
            //입장한 채팅방의 가장 최근 messageId를 가지고와서,
            Long latestMessageId = messageRepository.findLatestMessageId(chatroomMemberDTO.getChatroomId());
            //입장
            chatroomMemberDTO.setLastRead(latestMessageId);
            updateChatroomMember(chatroomMemberDTO.getUserId(), chatroomMemberDTO);
            chatroomManager.addSession(webSocketSession);
        }

    }


    @Override
    public ChatroomMembers createChatroomMember(ChatroomMemberDTO chatroomMemberDTO) {
        //방이 있는지 확인
        Chatrooms chatroom = chatroomRepository.findById(chatroomMemberDTO.getChatroomId()).orElseThrow(() -> new ChatroomNotFoundException(ErrorCode.CHATROOM_NOT_FOUND));
        //유저가 실제로 있는지 확인
        Users user = userRepository.findById(chatroomMemberDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        //이 두개가 채팅멤버의 unique조건. 중복되면 안되기때문에 있는지 확인
        if(chatroomMemberRepository.existsByChatroomAndUser(chatroom,user)){
            throw new DuplicateChatMemberException(ErrorCode.DUPLICATED_CHAT_MEMBER);
        }
        //입장한 방의 가장 최신 messageId를 가지고 옴, null일 수 있지만 entity변환시 0으로 바꾸기 때문에 상관없음
        Long latestMessageId = messageRepository.findLatestMessageId(chatroomMemberDTO.getChatroomId());
        
        //입장한 순간의 메시지id 기록(이 사람은 여기서부터 메시지를 읽을 수 있음. 입장 전의 메시지는 확인하지 못함)
        chatroomMemberDTO.setStartRead(latestMessageId);
        //입장한 순간 lastRead를 최신화
        chatroomMemberDTO.setLastRead(latestMessageId);
        //엔티티 변환
        ChatroomMembers chatroomMember = toEntity(chatroomMemberDTO);
        //저장 및 리턴
        return chatroomMemberRepository.save(chatroomMember);
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
    public void deleteChatroomMember(Long userId, Long chatroomId) {
        //TODO 인증처리
        chatroomMemberRepository.deleteByUser_UserIdAndChatroom_ChatroomId(userId, chatroomId);
    }

    @Override
    public boolean checkChatroomMember(Long userId, Long chatroomId) {
        return chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId, userId, false);
    }

    @Override
    public Long countChatroomMember(Long chatroomId) {
        return chatroomMemberRepository.countChatroomMembersByChatroom_ChatroomIdAndIsBanned(chatroomId, false);
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
                .build();
    }

    private ChatroomMemberDTO toDTO(ChatroomMembers chatroomMembers) {
        return ChatroomMemberDTO.builder()
                .chatroomMemberId(chatroomMembers.getChatroomMemeberId())
                .userId(chatroomMembers.getUser().getUserId())
                .chatroomId(chatroomMembers.getChatroom().getChatroomId())
                .startRead(chatroomMembers.getStartRead() != null ? chatroomMembers.getStartRead() : 0)
                .lastRead(chatroomMembers.getLastRead() != null ? chatroomMembers.getLastRead() : 0)
                .noteOff(chatroomMembers.isNoteOff())
                .role(chatroomMembers.getRole())
                .isBanned(chatroomMembers.isBanned())
                .build();
    }

    private void checkChatMember(Long chatroomId, Long userId){
        boolean result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId, userId, false);
        if(!result){
            throw new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER);
        }
    }
}
