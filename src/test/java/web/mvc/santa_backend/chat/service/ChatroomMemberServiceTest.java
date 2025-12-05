package web.mvc.santa_backend.chat.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.ChatroomMemberDTO;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.repository.ChatroomMemberRepository;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class ChatroomMemberServiceTest {

    @Autowired
    private ChatroomMemberService chatroomMemberService;

    @Autowired
    private ChatroomMemberRepository chatroomMemberRepository;


    @Test
    @Transactional(readOnly = true)
    public void getChatroomMembersTest(){
        //given
        Long chatroomId = 1L;
        boolean banned = false;
        //when
        List<UserSimpleDTO> chatroomMembers = chatroomMemberService.getChatroomMembers(chatroomId,banned);

        //then
        Assertions.assertNotNull(chatroomMembers);

        for(UserSimpleDTO userSimpleDTO:chatroomMembers){
            System.out.println(userSimpleDTO);
        }
    }

    @Test
    public void createChatroomMemberTest(){
        //given
        //Long chatroomId = 1L;
        //Long userId = 1L;
        Long chatroomId = 3L;
        Long userId = 1L;
        ChatroomMemberDTO test = ChatroomMemberDTO.builder().userId(userId).chatroomId(chatroomId).build();

        //when
        chatroomMemberService.createChatroomMember(test);
        ChatroomMembers testMember = chatroomMemberRepository.findByChatroom_ChatroomIdAndUser_UserId(chatroomId, userId).orElseThrow(RuntimeException::new);
        //then
        Assertions.assertNotNull(testMember);
    }


}
