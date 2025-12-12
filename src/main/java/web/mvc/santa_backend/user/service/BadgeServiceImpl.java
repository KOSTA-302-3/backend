package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.InvalidException;
import web.mvc.santa_backend.common.exception.NotFoundException;
import web.mvc.santa_backend.user.dto.BadgeDTO;
import web.mvc.santa_backend.user.dto.UserBadgeDTO;
import web.mvc.santa_backend.user.entity.Badges;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.entity.Users_Badges;
import web.mvc.santa_backend.user.repository.BadgeRepository;
import web.mvc.santa_backend.user.repository.UserBadgeRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<BadgeDTO> getBadges(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Badges> badges = badgeRepository.findAll(pageable);

        return badges.map(badge -> modelMapper.map(badges, BadgeDTO.class));
    }

    @Override
    public Page<BadgeDTO> getBadgesByUserId(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Users_Badges> usersBadges = userBadgeRepository.findByUserId(userId, pageable);

        //Page<BadgeDTO> ownBadges = usersBadges.map(ub -> modelMapper.map(ub, BadgeDTO.class));
        return usersBadges.map(ub -> modelMapper.map(ub.getBadge(), BadgeDTO.class));
    }

    @Override
    public UserBadgeDTO buyBadge(Long userId, Long badgeId) {
        Users user = userRepository.findById(userId).orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Badges badge = badgeRepository.findById(badgeId).orElseThrow(()->new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

        // 유저의 point 가 배지의 price 보다 많은지 비교
        if (user.getPoint() < badge.getPrice()) throw new InvalidException(ErrorCode.INVALID_BUY);

        Users_Badges ub = Users_Badges.builder()
                .user(user)
                .badge(badge)
                .createdAt(LocalDateTime.now())
                .build();
        Users_Badges saved = userBadgeRepository.save(ub);

        return modelMapper.map(saved, UserBadgeDTO.class);
    }
}
