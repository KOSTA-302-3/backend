package web.mvc.santa_backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.chat.dto.ChatMessage;

@Service
@RequiredArgsConstructor
public class MessagePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(web.mvc.santa_backend.chat.dto.ChatMessage message){
        rabbitTemplate.convertAndSend("chat.exchange","", message);
    }
}
