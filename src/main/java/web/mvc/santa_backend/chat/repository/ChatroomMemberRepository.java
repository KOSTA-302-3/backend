package web.mvc.santa_backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import web.mvc.santa_backend.chat.entity.ChatroomMembers;
import web.mvc.santa_backend.chat.entity.Chatrooms;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;
import java.util.Optional;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMembers,Long> {
    @Query("select c from ChatroomMembers cm join cm.chatroom c where cm.user.userId = :userId")
    Page<Chatrooms> findByUserId(Long userId, Pageable pageable);

    @Query("select c from ChatroomMembers cm join cm.chatroom c where cm.user.userId = :userId and lower(c.name) like lower(concat('%', :word, '%'))")
    Page<Chatrooms> findByUserIdAndWord(Long userId, String word, Pageable pageable);

    List<ChatroomMembers> findByChatroomAndIsBanned(Chatrooms chatroom, boolean isBanned);

    Optional<ChatroomMembers> findByChatroom_ChatroomIdAndUser_UserId(Long chatroomId, Long userId);

    boolean existsByChatroomAndUser(Chatrooms chatroom, Users user);

    Optional<ChatroomMembers> findByUserAndChatroom(Users user, Chatrooms chatroom);

    boolean existsByChatroomAndUserAndIsBanned(Chatrooms chatroom, Users user, boolean isBanned);

    void deleteByUserAndChatroom(Users user, Chatrooms chatroom);

    boolean existsByChatroom_ChatroomIdAndUser_UserIdAndIsBanned(Long chatroomChatroomId, Long userUserId, boolean isBanned);

    void deleteByUser_UserIdAndChatroom_ChatroomId(Long userUserId, Long chatroomChatroomId);

    Long countByChatroom_ChatroomIdAndIsBannedAndLastReadLessThan(Long chatroomChatroomId, boolean isBanned, Long lastReadIsLessThan);

    Long countChatroomMembersByChatroom_ChatroomIdAndIsBanned(Long chatroomChatroomId, boolean isBanned);

    long countByChatroom_ChatroomIdAndIsBanned(Long chatroomChatroomId, boolean isBanned);
}
