package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.ChatroomDTO;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.ChatroomRepository;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.ChatMemberNotFoundException;
import web.mvc.santa_backend.common.exception.ChatroomNotFoundException;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.ForbiddenException;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class ChatroomServiceImpl implements ChatroomService {
    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;

    @Override
    public Long createChatroom(ChatroomDTO chatroomDTO) {
        Chatrooms chatrooms = toEntity(chatroomDTO);
        Chatrooms save = chatroomRepository.save(chatrooms);
        return save.getChatroomId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatroomDTO> getChatrooms(Long userId, String word, int page) {

        Page<Chatrooms> chatrooms = null;
        Pageable pageable = PageRequest.of(page, 10);
        if(userId==null && word==null){
            chatrooms = chatroomRepository.findByIsPrivateAndIsDeleted(false, false, pageable);
        }else if(userId==null && word!=null){
            chatrooms = chatroomRepository.findByWord(word, pageable);
        }else if(userId!=null && word==null){
            chatrooms = chatroomMemberRepository.findByUserId(userId, false, pageable);
        }else if(userId!=null && word!=null){
            chatrooms = chatroomMemberRepository.findByUserIdAndWord(userId, word, false, pageable);
        }

        Page<ChatroomDTO> chatroomDTOS = chatrooms.map((n) -> {
            long count = chatroomMemberRepository.countByChatroom_ChatroomIdAndIsBanned(n.getChatroomId(), false);
            ChatroomDTO chatroomDTO = toDTO(n, count);
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

    private Chatrooms toEntity(ChatroomDTO chatroomDTO) {
        return Chatrooms.builder()
                .name(chatroomDTO.getName())
                .isPrivate(chatroomDTO.getIsPrivate() != null ? chatroomDTO.getIsPrivate() : false)
                .password(chatroomDTO.getPassword())
                .imageUrl(chatroomDTO.getImageUrl() != null ?  chatroomDTO.getImageUrl() : "")
                .description(chatroomDTO.getDescription())
                .build();
    }

    private ChatroomDTO toDTO(Chatrooms chatrooms, long count) {
        return ChatroomDTO.builder()
                .chatroomId(chatrooms.getChatroomId())
                .name(chatrooms.getName())
                .createdAt(chatrooms.getCreatedAt())
                .isPrivate(chatrooms.isPrivate())
                .password(chatrooms.getPassword())
                .isDeleted(chatrooms.isDeleted())
                .imageUrl(chatrooms.getImageUrl())
                .description(chatrooms.getDescription())
                .countMember(count)
                .build();
    }
}
