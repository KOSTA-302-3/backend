package web.mvc.santa_backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import web.mvc.santa_backend.chat.entity.Chatrooms;

public interface ChatroomRepository extends JpaRepository<Chatrooms, Long> {
    @Query("select c from Chatrooms c where c.isPrivate = false and c.isDeleted = false and lower(c.name) like lower(concat('%', :word, '%'))")
    Page<Chatrooms> findByWord(String word, Pageable pageable);

    Page<Chatrooms> findByIsPrivateAndIsDeleted(boolean isPrivate, boolean isDeleted);
}
