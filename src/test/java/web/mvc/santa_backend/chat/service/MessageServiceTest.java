package web.mvc.santa_backend.chat.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.chat.dto.InboundChatMessageDTO;
import web.mvc.santa_backend.chat.dto.OutboundChatMessageDTO;
import web.mvc.santa_backend.chat.repository.MessageRepository;
import web.mvc.santa_backend.common.enumtype.MessageType;

@SpringBootTest
@Transactional
public class MessageServiceTest {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void createMessageTest() {
        //given
        InboundChatMessageDTO message = InboundChatMessageDTO.builder()
                .userId(1L)
                .chatroomId(1L)
                .payload("test message")
                .type(MessageType.TEXT)
                .build();
        //when
        OutboundChatMessageDTO testmessage = messageService.createMessage(message);
        //then
        Assertions.assertNotNull(testmessage);
    }
}
