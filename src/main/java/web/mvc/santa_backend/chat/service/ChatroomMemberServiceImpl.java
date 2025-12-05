package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.chat.repository.ChatroomRepository;
import web.mvc.santa_backend.common.enumtype.UserRole;
import web.mvc.santa_backend.common.exception.*;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatroomMemberServiceImpl implements ChatroomMemberService {
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserSimpleDTO> getChatroomMembers(Long chatroomId, boolean isBanned) {
        List<ChatroomMembers> chatroomMembers = chatroomMemberRepository.findByChatroomAndIsBanned(Chatrooms.builder().chatroomId(chatroomId).build(), isBanned);
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
    public void createChatroomMember(ChatroomMemberDTO chatroomMemberDTO) {
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
        //저장
        chatroomMemberRepository.save(chatroomMember);
    }

    @Override
    public void updateChatroomMember(Long userId, ChatroomMemberDTO chatroomMemberDTO) {

        ChatroomMembers chatroomMember = chatroomMemberRepository.findById(chatroomMemberDTO.getChatroomMemberId()).orElseThrow(() -> new ChatMemberNotFoundException(ErrorCode.CHATMEMBER_NOT_FOUND));
        //최근 읽은 글 업데이트
        if(chatroomMemberDTO.getLastRead() != null){
            chatroomMember.setLastRead(chatroomMemberDTO.getLastRead());
        }
        //알림 on/off 설정
        if(chatroomMemberDTO.getNoteOff() != null){
            chatroomMember.setNoteOff(chatroomMemberDTO.getNoteOff());
        }
        //강제 퇴장(Admin만 가능)
        if(chatroomMemberDTO.getIsBanned() != null && chatroomMemberDTO.getRole().equals(UserRole.ADMIN)){
            chatroomMember.setBanned(chatroomMemberDTO.getIsBanned());
        }
        //role 변경... 고민중..
        if(chatroomMemberDTO.getRole() != null){
            chatroomMember.setRole(chatroomMemberDTO.getRole());
        }
    }

    @Override
    public void deleteChatroomMember(Long chatroomMemberId) {

    }

    private ChatroomMembers toEntity(ChatroomMemberDTO chatroomMemberDTO) {
        Chatrooms chatroom = Chatrooms.builder().chatroomId(chatroomMemberDTO.getChatroomId()).build();
        Users user = Users.builder()
                .userId(chatroomMemberDTO.getUserId())
                .build();
        return ChatroomMembers.builder()
                .chatroom(chatroom)
                .user(user)
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
}
