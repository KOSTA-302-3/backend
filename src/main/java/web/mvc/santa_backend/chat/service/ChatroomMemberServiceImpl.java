package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
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

    @Override
    @Transactional(readOnly = true)
    public List<UserSimpleDTO> getChatroomMembers(Long chatroomId, boolean isBanned, Long userId) {
        //먼저 방 참여자인지의 검증
        checkChatMember(chatroomId, userId);
        //chatroomId로 현재유저/강퇴유저를 isBanned로 구별해서 불러오기
        List<ChatroomMembers> chatroomMembers =
                chatroomMemberRepository.findByChatroomAndIsBanned(Chatrooms.builder().chatroomId(chatroomId).build(), isBanned);

        List<UserSimpleDTO> userSimpleDTOList = new ArrayList<>();

        //chatroomMember에 들어있는 user엔티티를... userSimpleDTO로 변환후 리스트에 add
        for(ChatroomMembers m : chatroomMembers){
            userSimpleDTOList.add(
                    UserSimpleDTO.builder()
                    .userId(m.getUser().getUserId())
                    .username(m.getUser().getUsername())
                    .profileImage(m.getUser().getProfileImage())
                    .build()
            );
        }
        return userSimpleDTOList;
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
        //참여자가 아니라면 참여멤버 테이블에 레코드 추가
        if(!result){
            //참여자가 0명이라면 방을 만든사람 -> 즉 Admin
            long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(chatroomMemberDTO.getChatroomId(), false);
            if(count == 0){
                chatroomMemberDTO.setRole(UserRole.ADMIN);
            }else {
                chatroomMemberDTO.setRole(UserRole.USER);
            }
            ChatroomMembers chatroomMember = createChatroomMember(chatroomMemberDTO);
            chatroomManager.addSession(webSocketSession);
            Users user = userRepository.findById(chatroomMember.getUser().getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
            InboundChatMessageDTO message = InboundChatMessageDTO.builder()
                    .userId(chatroomMember.getUser().getUserId())
                    .chatroomId(chatroomMember.getChatroom().getChatroomId())
                    .payload(user.getUsername() + "님 입장")
                    .type(MessageType.NOTICE)
                    .build();
            OutboundChatMessageDTO outMessage = messageService.createMessage(message);
            log.info(outMessage.toString());
            chatroomManager.broadcast(outMessage);
        }else {
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
