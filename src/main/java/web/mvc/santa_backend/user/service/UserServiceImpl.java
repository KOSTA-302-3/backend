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
import web.mvc.santa_backend.user.entity.Follows;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.FollowRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CustomService customService;
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
        customService.addCustom(newUser);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserSimpleDTO> getUsersByUsername(String username, int page) {

        // 페이징
        log.info("getUsersByUsername/ page: {}", page);
        Pageable pageable = PageRequest.of(page, 10);
        //Pageable pageable = PageRequest.of(page, 10, 정렬방향, 정렬기준필드);

        //Page<Users> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        Page<Users> users = userRepository.findWithCustomByUsername(username, pageable);
        Page<UserSimpleDTO> userSimpleDTOs = users.map(user -> modelMapper.map(user, UserSimpleDTO.class));

        return userSimpleDTOs;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getUserById(Long id) {
        //Users user = userRepository.findById(id)
        Users user = userRepository.findWithCustomById(id)
                //.orElseThrow(()->new RuntimeException(ErrorCode.USER_NOTFOUND))
                .orElseThrow(()->new RuntimeException("해당 유저가 없습니다."));

        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        userResponseDTO.setFollowingList(this.getFollowings(id));
        userResponseDTO.setFollowerList(this.getFollowers(id));

        return userResponseDTO;
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

        // TODO: save로수정
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
    public UserResponseDTO deactivateUser(Long id) {
        Users user = userRepository.findById(id)
                //.orElseThrow(()->new DMLException(ErrorCode.));
                .orElseThrow(()->new RuntimeException("탈퇴 실패"));
        user.setState(false);   // 상태 비활성화로 변경
        user.setDeletedAt(LocalDateTime.now());

        // 현재 유저를 팔로우 하고 있는 유저들의 팔로잉 수 -1
        this.getFollowers(id).forEach(follower -> userRepository.decreaseFollowingCount(follower.getUserId()));
        // 현재 유저가 팔로우 하고 있는 유저들의 팔로워 수 -1
        this.getFollowings(id).forEach(following -> userRepository.decreaseFollowerCount(following.getUserId()));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    @Override
    public UserResponseDTO reactivateUser(Long id) {
        Users user = userRepository.findById(id)
                //.orElseThrow(()->new DMLException(ErrorCode.));
                .orElseThrow(()->new RuntimeException("복구 실패"));
        user.setState(true);
        user.setDeletedAt(null);

        // 현재 유저를 팔로우 하고 있는 유저들의 팔로잉 수 +1
        this.getFollowers(id).forEach(follower -> userRepository.increaseFollowingCount(follower.getUserId()));
        // 현재 유저가 팔로우 하고 있는 유저들의 팔로워 수 +1
        this.getFollowings(id).forEach(following -> userRepository.increaseFollowerCount(following.getUserId()));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("삭제 실패"));
        userRepository.deleteById(id);
    }

    @Override
    public List<UserSimpleDTO> getFollowings(Long id) {
        List<Follows> follows = followRepository.findByFollower_UserIdAndFollowing_StateIsTrueAndPendingIsFalse(id);

        // Follows -> Follows.following (Users) -> UserSimpleDTO
        List<UserSimpleDTO> followings = follows.stream()
                .map(follow -> modelMapper.map(follow.getFollowing(), UserSimpleDTO.class))
                .toList();

        return followings;
    }

    @Override
    public List<UserSimpleDTO> getFollowers(Long id) {
        List<Follows> follows = followRepository.findByFollowing_UserIdAndFollower_StateIsTrueAndPendingIsFalse(id);

        // Follows -> Follows.following (Users) -> UserSimpleDTO
        List<UserSimpleDTO> followers = follows.stream()
                .map(follow -> modelMapper.map(follow.getFollower(), UserSimpleDTO.class))
                .toList();

        return followers;
    }

    @Override
    public Page<UserSimpleDTO> getFollowings(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Follows> follows = followRepository.findByFollower_UserIdAndFollowing_StateIsTrueAndPendingIsFalse(id, pageable);

        // Follows -> Follows.following (Users) -> UserSimpleDTO
        Page<UserSimpleDTO> followings = follows
                .map(follow -> modelMapper.map(follow.getFollowing(), UserSimpleDTO.class));

        return followings;
    }

    @Override
    public Page<UserSimpleDTO> getFollowers(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Follows> follows = followRepository.findByFollowing_UserIdAndFollower_StateIsTrueAndPendingIsFalse(id, pageable);

        // Follows -> Follows.following (Users) -> UserSimpleDTO
        Page<UserSimpleDTO> followers = follows
                .map(follow -> modelMapper.map(follow.getFollower(), UserSimpleDTO.class));

        return followers;
    }

    @Override
    public Page<UserSimpleDTO> getPendingFollowers(Long id, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        Page<Follows> follows = followRepository.findByFollowing_UserIdAndPendingIsTrue(id, pageable);

        // Follows -> Follows.following (Users) -> UserSimpleDTO
        Page<UserSimpleDTO> followers = follows
                .map(follow -> modelMapper.map(follow.getFollower(), UserSimpleDTO.class));

        return followers;
    }
}
