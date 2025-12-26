package web.mvc.santa_backend.chat.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.chat.repository.ChatroomRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChatroomServiceTest {

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Test
    public void createChatroom() {
        Chatrooms test = Chatrooms.builder()
                .name("test")
                .build();
        Chatrooms save = chatroomRepository.save(test);

        assertThat(save).isNotNull();
        assertThat(save.getName()).isEqualTo(test.getName());
    }
}