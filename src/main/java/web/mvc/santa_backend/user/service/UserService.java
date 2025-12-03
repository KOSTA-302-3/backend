package web.mvc.santa_backend.user.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserRequestDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;

public interface UserService {
    /**
     * 아이디(username) 중복체크
     */
    String checkUsernameDuplication(String username);

    /**
     * 이메일 중복체크
     */
    String checkEmailDuplication(String email);

    /**
     * 회원가입
     */
    void register(UserRequestDTO userDTO);

    /**
     * 아이디(username)로 유저 조회
     */
    Page<UserSimpleDTO> getUsersByUsername(String username, int page);

    /**
     * userId로 개인 유저 조회
     */
    UserResponseDTO getUserById(Long id);


    /**
     * 유저 정보 수정
     */
    UserResponseDTO updateUsers(Long id, UserRequestDTO userDTO);

    /**
     * 유저 탈퇴
     */
    UserResponseDTO deactivateUser(Long id);

    /**
     * 유저 삭제
     */
    void deleteUser(Long id);
}
