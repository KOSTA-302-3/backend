package web.mvc.santa_backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.mvc.santa_backend.chat.entity.Messages;

import java.util.List;

public interface MessageRepository extends JpaRepository<Messages,Long> {
    Page<Messages> findByChatrooms_ChatroomId(Long chatroomsChatroomId, Pageable pageable);

    @Query("SELECT MAX(m.messageId) FROM Messages m WHERE m.chatrooms.chatroomId = :chatroomId")
    Long findLatestMessageId(@Param("chatroomId") Long chatroomId);

    Page<Messages> findByChatrooms_ChatroomIdAndMessageIdGreaterThan(Long chatroomsChatroomId, Long messageIdIsGreaterThan, Pageable pageable);

    Page<Messages> findByChatrooms_ChatroomIdAndMessageIdGreaterThanOrderByUserIdDesc(Long chatroomsChatroomId, Long messageIdIsGreaterThan, Pageable pageable);
}
