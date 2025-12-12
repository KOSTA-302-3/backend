package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.mvc.santa_backend.common.enumtype.CustomItemType;
import web.mvc.santa_backend.common.exception.ErrorCode;
import web.mvc.santa_backend.common.exception.NotFoundException;
import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.entity.Badges;
import web.mvc.santa_backend.user.entity.Colors;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.CustomRepository;
import web.mvc.santa_backend.user.repository.UserBadgeRepository;
import web.mvc.santa_backend.user.repository.UserColorRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomServiceImpl implements CustomService {

    private final CustomRepository customRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserColorRepository userColorRepository;
    private final ModelMapper modelMapper;

    @Override
    public CustomDTO addCustom(Users user) {
        Customs custom = Customs.builder()
                .user(user)
                // TODO: Add Default Badge or Default Color
                .build();
        Customs saveCustom = customRepository.save(custom);

        return modelMapper.map(saveCustom, CustomDTO.class);
    }

    @Override
    public CustomDTO updateCustom(CustomItemType type, Long userId, Long targetId) {
        Customs customs = customRepository.findById(userId)
                .orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));

        switch (type) {
            case BADGE -> {
                // 소유하고 있는지 먼저 확인
                Badges badge = userBadgeRepository.findByUser_UserIdAndBadge_BadgeId(userId, targetId)
                        .orElseThrow(()->new NotFoundException(ErrorCode.ITEM_NOT_FOUND))
                        .getBadge();
                customs.setBadge(badge);
            }
            case COLOR -> {
                Colors color = userColorRepository.findByUser_UserIdAndColor_ColorId(userId, targetId)
                        .orElseThrow(()->new NotFoundException(ErrorCode.ITEM_NOT_FOUND))
                        .getColor();
                customs.setColor(color);
            }
        }
        return modelMapper.map(customs, CustomDTO.class);
    }
}
