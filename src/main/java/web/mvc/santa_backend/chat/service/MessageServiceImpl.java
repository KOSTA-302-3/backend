package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.ReadUpdateDTO;
import web.mvc.santa_backend.chat.dto.ReplyMessageDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.entity.Messages;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.MessageRepository;
import web.mvc.santa_backend.common.enumtype.MessageType;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<OutboundChatMessageDTO> getOutboundChatMessages(Long chatroomId, Long userId, int page) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomId, userId)
                .orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER));
        Long startRead = chatroomMember.getStartRead();
        Pageable pageable = PageRequest.of(page,100, Sort.by(Sort.Direction.DESC, "messageId"));
        Page<Messages> chatMessageList =
                messageRepository.findByChatrooms_ChatroomIdAndMessageIdGreaterThan(chatroomId, startRead, pageable);

        return chatMessageList.map(c -> toOutboundChatMessageDTO(c));
    }

    @Override
    public OutboundChatMessageDTO createMessage(InboundChatMessageDTO inboundChatMessageDTO) {
        checkChatMember(inboundChatMessageDTO.getChatroomId(),inboundChatMessageDTO.getUserId());
        Messages message = messageRepository.save(fromInboundMessageDTOToEntity(inboundChatMessageDTO));
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

    private void checkChatMember(Long chatroomId, Long userId){
        boolean result = chatroomMemberRepository.existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(chatroomId,userId,false);
        if(!result){
            throw new ChatMemberNotFoundException(ErrorCode.NOT_CHATMEMBER);
        }
    }

    private OutboundChatMessageDTO toOutboundChatMessageDTO(Messages messages){
        long unreadCount = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBannedAndLastReadLessThan(
                messages.getChatrooms().getChatroomId(), false, messages.getMessageId());
        OutboundChatMessageDTO outboundChatMessageDTO = OutboundChatMessageDTO.builder()
                .messageId(messages.getMessageId())
                .userId(messages.getUserId())
                .chatroomId(messages.getChatrooms().getChatroomId())
                .payload(messages.getPayload())
                .createdAt(messages.getCreatedAt())
                .type(messages.getType())
                .unreadCount(unreadCount)
                .build();
        if(messages.getReplyMessage() != null){
            outboundChatMessageDTO.setReplyMessage(toReplyMessageDTO(messages.getReplyMessage()));
        }
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
