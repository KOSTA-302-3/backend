package web.mvc.santa_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.mvc.santa_backend.user.entity.Users;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    Users findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /*@Query("select u from Users u join fetch u.custom c " +
            "where lower(u.username) like lower(concat('%', :username, '%'))")
    Page<Users> findWithCustomByUsername(String username, Pageable pageable);*/
}
