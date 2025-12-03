package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserRequestDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public String checkUsernameDuplication(String username) {
        if (userRepository.existsByUsername(username)) {
            log.info("username: {}", username);
            return "이미 등록된 아이디입니다.";
        }
        return "사용 가능합니다.";
    }

    @Transactional(readOnly = true)
    @Override
    public String checkEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            log.info("email: {}", email);
            return "이미 등록된 이메일입니다.";
        }
        return "사용 가능합니다.";
    }

    @Transactional
    @Override
    public void register(UserRequestDTO userRequestDTO) {
        // 중복 체크
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new RuntimeException("아이디 중복");
            //throw new UserAuthenticationException(ErrorCode.DUPLICATEUSERNAME);
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("이메일 중복");
            //throw new UserAuthenticationException(ErrorCode.DUPLICATEEMAIL);
        }

        userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        Users newUser = userRepository.save(modelMapper.map(userRequestDTO, Users.class));
        log.info("user: {}", newUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserSimpleDTO> getUsersByUsername(String username, int page) {

        // 페이징
        log.info("getUsersByUsername/ page: {}", page);
        Pageable pageable = PageRequest.of(page, 10);
        //Pageable pageable = PageRequest.of(page, 10, 정렬방향, 정렬기준필드);

        Page<Users> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        Page<UserSimpleDTO> userSimpleDTOs = users.map(user -> modelMapper.map(user, UserSimpleDTO.class));

        return userSimpleDTOs;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getUserById(Long id) {
        Users user = userRepository.findById(id)
                //.orElseThrow(()->new RuntimeException(ErrorCode.USER_NOTFOUND))
                .orElseThrow(()->new RuntimeException("해당 유저가 없습니다."));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUsers(Long id, UserRequestDTO userRequestDTO) {
        log.info("updateUsers/ user: {}", userRequestDTO);

        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new RuntimeException("아이디 중복");
            //throw new UserAuthenticationException(ErrorCode.DUPLICATEUSERNAME);
        }

        Users user = userRepository.findById(id)
                //.orElseThrow(()->new DMLException(ErrorCode.));
                .orElseThrow(()->new RuntimeException("수정 실패"));
        user.setUsername(userRequestDTO.getUsername());
        user.setProfileImage(userRequestDTO.getProfileImage());
        user.setDescription(userRequestDTO.getDescription());
        user.setLevel(userRequestDTO.getLevel());
        user.setPrivate(userRequestDTO.isPrivate());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword())); // TODO: 비밀번호 암호화 수준 확인 및 이전 비밀번호 불가

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    @Override
    public UserResponseDTO deleteUser(Long id) {
        Users user = userRepository.findById(id)
                //.orElseThrow(()->new DMLException(ErrorCode.));
                .orElseThrow(()->new RuntimeException("탈퇴 실패"));
        user.setState(false);   // 상태 비활성화로 변경
        user.setDeletedAt(LocalDateTime.now());

        return modelMapper.map(user, UserResponseDTO.class);
    }


    /**
     * DTO -> Entity
     * (UserRequestDTO -> Users)
     */
/*    private Users toEntity(UserRequestDTO UserRequestDTO) {
        return Users.builder()
                .username(UserRequestDTO.getUsername())
                .password(UserRequestDTO.getPassword())
                .email(UserRequestDTO.getEmail())
                .phone(UserRequestDTO.getPhone())
                .profileImage(UserRequestDTO.getProfileImage())
                .description(UserRequestDTO.getDescription())
                .level(UserRequestDTO.getLevel())
                .followerCount(0L)
                .followingCount(0L)
                .build();
    }*/

    /**
     * Entity -> DTO
     * (Users -> UserResponseDTO)
     */
//    private UserResponseDTO toDTO(Users users) {
//        // Users 에는.. followingList, blockedList, Custom에 대한 정보가 없는데..어떻게 가져오는 거지? join..
//        return UserResponseDTO.builder()
//                .userId(users.getUserId())
//                .username(users.getUsername())
//                .password(users.getPassword())
//                .email(users.getEmail())
//                .phone(users.getPhone())
//                .profileImage(users.getProfileImage())
//                .description(users.getDescription())
//                .followerCnt(users.getFollowerCount())
//                .followingCnt(users.getFollowingCount())
//                .point(users.getPoint())
//                .level(users.getLevel())
//                .state(users.isState())
//                .isPrivate(users.isPrivate())
//                .createdAt(users.getCreatedAt())
//                .deletedAt(users.getDeletedAt())
//                .build();
//    }
}
