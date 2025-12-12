package web.mvc.santa_backend.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.admin.dto.AdminDTO;
import web.mvc.santa_backend.admin.entity.AdminEntity;
import web.mvc.santa_backend.admin.repository.AdminRepository;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.UserRepository;
import web.mvc.santa_backend.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * 전체 유저 목록 조회
     */
    @Override
    public Page<UserSimpleDTO> getAllUsers(int page) {
        log.info("getAllUsers/ page: {}", page);
        Pageable pageable = PageRequest.of(page, 20);
        Page<Users> usersPage = userRepository.findAll(pageable);
        
        List<UserSimpleDTO> userList = usersPage.getContent().stream()
                .map(user -> UserSimpleDTO.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .profileImage(user.getProfileImage())
                        .build())
                .collect(Collectors.toList());
        
        return new PageImpl<>(userList, pageable, usersPage.getTotalElements());
    }

    /**
     * 유저 상세 조회 (정지 기간 만료 시 자동 해제)
     */
    @Override
    public UserResponseDTO getUserDetail(Long userId) {
        log.info("getUserDetail/ userId: {}", userId);
        
        // 정지 기간 체크 및 자동 해제
        checkAndReleaseBan(userId);
        
        return userService.getUserById(userId);
    }

    /**
     * 유저 정지
     */
    @Override
    public AdminDTO suspendUser(Long userId, int days, String category, String detail) {
        log.info("suspendUser/ userId: {}, days: {}, category: {}", userId, days, category);
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        
        // 정지 종료 날짜 계산
        LocalDateTime finishedAt;
        if (days == -1) {
            // 영구 정지
            finishedAt = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        } else {
            finishedAt = LocalDateTime.now().plusDays(days);
        }
        
        // 정지 기록 생성
        AdminEntity ban = AdminEntity.builder()
                .user(user)
                .category(category)
                .detail(detail)
                .finishedAt(finishedAt)
                .build();
        
        AdminEntity savedBan = adminRepository.save(ban);
        
        // 유저 정지 처리
        user.setState(false);
        userRepository.save(user);
        
        return AdminDTO.builder()
                .banId(savedBan.getBanId())
                .userId(savedBan.getUser().getUserId())
                .category(savedBan.getCategory())
                .detail(savedBan.getDetail())
                .createdAt(savedBan.getCreatedAt())
                .finishedAt(savedBan.getFinishedAt())
                .build();
    }

    /**
     * 유저 정지 해제
     */
    @Override
    public void activateUser(Long userId) {
        log.info("activateUser/ userId: {}", userId);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.setState(true);
        userRepository.save(user);
    }

    /**
     * 유저 탈퇴 처리 (소프트 삭제)
     */
    @Override
    public UserResponseDTO deactivateUser(Long userId) {
        log.info("deactivateUser/ userId: {}", userId);
        return userService.deactivateUser(userId);
    }

    /**
     * 유저 정지 내역 조회
     */
    @Override
    public List<AdminDTO> getUserBans(Long userId) {
        log.info("getUserBans/ userId: {}", userId);
        return adminRepository.findByUser_UserId(userId).stream()
                .map(ban -> AdminDTO.builder()
                        .banId(ban.getBanId())
                        .userId(ban.getUser().getUserId())
                        .category(ban.getCategory())
                        .detail(ban.getDetail())
                        .createdAt(ban.getCreatedAt())
                        .finishedAt(ban.getFinishedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 정지 기간 확인 후 자동 해제 (Admin 전용)
     */
    @Transactional
    public void checkAndReleaseBan(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        
        if (!user.isState()) { // 정지 상태인 경우만 체크
            LocalDateTime now = LocalDateTime.now();
            List<AdminEntity> userBans = adminRepository.findByUser_UserId(userId);
            
            // 모든 정지가 만료되었는지 확인
            boolean allExpired = userBans.stream()
                    .allMatch(ban -> ban.getFinishedAt().isAfter(now));
            
            if (allExpired && !userBans.isEmpty()) {
                user.setState(true);
                userRepository.save(user);
                log.info("유저 정지 자동 해제: userId={}, username={}", user.getUserId(), user.getUsername());
            }
        }
    }
}
