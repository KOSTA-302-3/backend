package web.mvc.santa_backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import web.mvc.santa_backend.chat.entity.Chatrooms;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatrooms, Long> {
    @Query("select c from Chatrooms c where c.isPrivate = false and c.isDeleted = false and lower(c.name) like lower(concat('%', :word, '%'))")
    Page<Chatrooms> findByWord(String word, Pageable pageable);

    Page<Chatrooms> findByIsPrivateAndIsDeleted(boolean isPrivate, boolean isDeleted, Pageable pageable);

    @Query("""
    select cr
    from Chatrooms cr
    where cr.chatroomType = web.mvc.santa_backend.common.enumtype.ChatroomType.DM
      and exists (
          select 1
          from ChatroomMembers m1
          where m1.chatroom = cr
            and m1.user.userId = :userId1
      )
      and exists (
          select 1
          from ChatroomMembers m2
          where m2.chatroom = cr
            and m2.user.userId = :userId2
      )
""")
    Optional<Chatrooms> findDmRoomByTwoUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );
}
