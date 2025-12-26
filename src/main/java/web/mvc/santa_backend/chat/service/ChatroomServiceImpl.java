package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.*;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.ChatroomRepository;
import web.mvc.santa_backend.chat.repository.MessageRepository;
import web.mvc.santa_backend.common.enumtype.NotificationType;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.*;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ChatroomServiceImpl implements ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    @Override
    public Long createChatroom(ChatroomRequestDTO chatroomRequestDTO) {
        Chatrooms chatrooms = toEntity(chatroomRequestDTO);
        Chatrooms save = chatroomRepository.save(chatrooms);
        return save.getChatroomId();
    }

    @Override
    public Long createChatroom(Long userId, Long myUserId) {
        if(myUserId.equals(userId)){
            throw new InvalidException(ErrorCode.WRONG_TARGET);
        }
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.INVALID_USER));
        Chatrooms chatroom = Chatrooms.builder()
                .name(user.getUsername() + "님과의 DM")
                .isPrivate(true)
                .isDeleted(false)
                .imageUrl("test")
                .description(user.getUsername() + "님과의 DM")
                .build();
        Chatrooms save = chatroomRepository.save(chatroom);
        Long chatroomId = save.getChatroomId();
        NotificationDTO notification = NotificationDTO.builder()
                .userId(userId)
                .actionUserId(myUserId)
                .type(NotificationType.DM)
                .build();
        notificationService.createNotification(notification);

        ChatroomMembers chatroomMember = ChatroomMembers.builder()
                .chatroom(Chatrooms.builder().chatroomId(chatroomId).build())
                .user(user)
                .startRead(0L)
                .lastRead(0L)
                .noteOff(false)
                .role(UserRole.USER)
                .isBanned(false)
                .joinNoticeSent(false)
                .build();
        chatroomMemberRepository.save(chatroomMember);
        return chatroomId;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatroomResponseDTO> getChatrooms(Long userId, String word, int page) {
        System.out.println("userId: " + userId);
        System.out.println("word: " + word);
        System.out.println("page: " + page);
        Page<Chatrooms> chatrooms = null;
        Pageable pageable = PageRequest.of(page, 10);
        if(userId==null && (word==null || word.isEmpty())){
            log.info("전체불러오기");
            chatrooms = chatroomRepository.findByIsPrivateAndIsDeleted(false, false, pageable);
        }else if(userId==null){
            log.info("검색시");
            chatrooms = chatroomRepository.findByWord(word, pageable);
        }else if((word==null || word.isEmpty())){
            log.info("내 채팅방");
            chatrooms = chatroomMemberRepository.findByUserId(userId, false, pageable);
        }else {
            log.info("내 채팅방 검색");
            chatrooms = chatroomMemberRepository.findByUserIdAndWord(userId, word, false, pageable);
        }

        System.out.println(chatrooms.getContent());

        Page<ChatroomResponseDTO> chatroomDTOS = chatrooms.map((n) -> {
            long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(n.getChatroomId(), false);
            boolean unread = messageRepository.existsUnreadMessage(userId, n.getChatroomId());
            ChatroomResponseDTO chatroomDTO = toDTO(n, count, unread);
            return chatroomDTO;
        });

        return chatroomDTOS;
    }

    @Override
    public void updateChatroom(ChatroomDTO chatroomDTO, Long userId) {
        ChatroomMembers chatroomMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomDTO.getChatroomId(), userId)
                .orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        if(!chatroomMember.getRole().equals(UserRole.ADMIN)){
            throw new ForbiddenException(ErrorCode.NOT_CHATROOM_ADMIN);
        }

        Chatrooms chatroom = chatroomRepository.findById(chatroomDTO.getChatroomId()).orElseThrow(() -> new ChatroomNotFoundException(ErrorCode.CHATROOM_NOT_FOUND));
        if(chatroomDTO.getName()!=null){
            chatroom.setName(chatroomDTO.getName());
        }
        if(chatroomDTO.getPassword()!=null){
            chatroom.setPassword(chatroomDTO.getPassword());
        }
        if(chatroomDTO.getIsPrivate()!=null){
            chatroom.setPrivate(chatroomDTO.getIsPrivate());
        }
        if(chatroomDTO.getDescription()!=null){
            chatroom.setDescription(chatroomDTO.getDescription());
        }
        if(chatroomDTO.getImageUrl()!=null){
            chatroom.setImageUrl(chatroomDTO.getImageUrl());
        }
    }

    @Override
    public void deleteChatroom(Long id) {
        Chatrooms chatroom = chatroomRepository.findById(id).orElseThrow(() -> new ChatroomNotFoundException(ErrorCode.CHATROOM_NOT_FOUND));
        chatroom.setDeleted(true);
    }



    private Chatrooms toEntity(ChatroomRequestDTO chatroomRequestDTO) {
        return Chatrooms.builder()
                .name(chatroomRequestDTO.getName())
                .isPrivate(chatroomRequestDTO.getIsPrivate() != null ? chatroomRequestDTO.getIsPrivate() : false)
                .password(chatroomRequestDTO.getPassword())
                .imageUrl(chatroomRequestDTO.getImageUrl() != null ?  chatroomRequestDTO.getImageUrl() : "")
                .description(chatroomRequestDTO.getDescription())
                .build();
    }

    private ChatroomResponseDTO toDTO(Chatrooms chatrooms, long count, boolean hasUnread) {
        return ChatroomResponseDTO.builder()
                .id(chatrooms.getChatroomId())
                .name(chatrooms.getName())
                .isPrivate(chatrooms.isPrivate())
                .imageUrl(chatrooms.getImageUrl())
                .membersCount(count)
                .hasUnread(hasUnread)
                .build();
    }


}
