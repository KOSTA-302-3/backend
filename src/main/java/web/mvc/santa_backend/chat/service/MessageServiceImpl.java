package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.ReadUpdateDTO;
import web.mvc.santa_backend.chat.dto.ReplyMessageDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.entity.Messages;
import web.mvc.santa_backend.chat.manager.ChatroomManager;
import web.mvc.santa_backend.chat.manager.NotificationManager;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.MessageRepository;
import web.mvc.santa_backend.common.enumtype.MessageType;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.UserNotFoundException;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final UserRepository userRepository;
    private final ChatroomManager chatroomManager;
    private final NotificationManager notificationManager;

    @Override
    @Transactional(readOnly = true)
    public Page<OutboundChatMessageDTO> getOutboundChatMessages(Long chatroomId, Long userId, int page) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomId, userId)
                .orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER));
        Long startRead = chatroomMember.getStartRead();
        Pageable pageable = PageRequest.of(page,100, Sort.by(Sort.Direction.DESC, "messageId"));
        Page<Messages> chatMessageList =
                messageRepository.findByChatrooms_ChatroomIdAndMessageIdGreaterThanOrderByMessageIdDesc(chatroomId, startRead, pageable);

        return chatMessageList.map(c -> toOutboundChatMessageDTO(c));
    }

    @Override
    public OutboundChatMessageDTO createMessage(InboundChatMessageDTO inboundChatMessageDTO) {
        //방에 있는지 검증
        checkChatMember(inboundChatMessageDTO.getChatroomId(),inboundChatMessageDTO.getUserId());
        //온 메시지 바로 DB에 저장
        Messages message = messageRepository.save(fromInboundMessageDTOToEntity(inboundChatMessageDTO));
        
        //저장된 메시지의 id와, 채팅방id를 추출
        Long lastMessage = message.getMessageId();
        Long roomId = message.getChatrooms().getChatroomId();
        
        //메모리에 저장되어있는 채팅방을 가지고와서
        Map<Long, WebSocketSession> roomSessions = chatroomManager.getRoomSessions(roomId);
        
        //저장되어있는 사람들의 userId(key값)으로 lastRead를 업데이트
        //즉. 현재 실제 접속중인 사람들의 lastRead를 실시간으로 업데이트함
        Set<Long> ids = roomSessions.keySet();
        ids.forEach(userId -> {
            ChatroomMembers chatroomMembers = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(roomId, userId).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER));
            chatroomMembers.setLastRead(lastMessage);
        });

        List<ChatroomMembers> members = chatroomMemberRepository.findByChatroom_ChatroomIdAndIsBanned(roomId, false);

        members.forEach(member -> {
            Long memberId = member.getUser().getUserId();
            if(!ids.contains(memberId)) {
                notificationManager.sendNewNotification(memberId, "Chat");
            }
        });


        //OutMessageDTO로 바꿔서 리턴
        return toOutboundChatMessageDTO(message);
    }

    @Override
    public ReadUpdateDTO updateFrontUnreadCount(Long chatroomId, Long userId) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomId, userId)
                .orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER));
        Long latestMessageId = messageRepository.findLatestMessageId(chatroomId);

        return ReadUpdateDTO.builder()
                .chatroomId(chatroomId)
                .userId(userId)
                .messageType(MessageType.READ_UPDATE)
                .lastReadFrom(chatroomMember.getLastRead())
                .lastReadTo(latestMessageId)
                .build();
    }

    @Override
    public Long countAllUnreadMessages(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    private void checkChatMember(Long chatroomId, Long userId){
        boolean result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId,userId,false);
        if(!result){
            throw new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER);
        }
    }

    private OutboundChatMessageDTO toOutboundChatMessageDTO(Messages messages) {
        long unreadCount = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBannedAndLastReadLessThan(
                messages.getChatrooms().getChatroomId(), false, messages.getMessageId());
        String type = null;
        if(MessageType.NOTICE.equals(messages.getType())){
            type = "notice";
        }else{
            type = "normal";
        }
        Users user = userRepository.findById(messages.getUserId()).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        OutboundChatMessageDTO outboundChatMessageDTO = OutboundChatMessageDTO.builder()
                .id(messages.getMessageId())
                .type(type)
                .userId(messages.getUserId())
                .username(user.getUsername())
                .avatarUrl(user.getProfileImage())
                .text(messages.getPayload())
                .ts(messages.getCreatedAt())
                .messageType(messages.getType())
                .unreadCount(unreadCount)
                .build();
        return outboundChatMessageDTO;
    }

    private ReplyMessageDTO toReplyMessageDTO(Messages messages){
        return ReplyMessageDTO.builder()
                .messageId(messages.getMessageId())
                .userId(messages.getUserId())
                .payload(messages.getPayload())
                .build();
    }

    private Messages fromInboundMessageDTOToEntity(InboundChatMessageDTO inboundChatMessageDTO){
        Messages messages = Messages.builder()
                .userId(inboundChatMessageDTO.getUserId())
                .chatrooms(Chatrooms.builder().chatroomId(inboundChatMessageDTO.getChatroomId()).build())
                .payload(inboundChatMessageDTO.getPayload())
                .type(inboundChatMessageDTO.getType())
                .build();
        if(inboundChatMessageDTO.getReplyMessageId() != null){
            messages.setReplyMessage(Messages.builder().messageId(inboundChatMessageDTO.getReplyMessageId()).build());
        }
        return messages;
    }
}
