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
import web.mvc.santa_backend.user.dto.ColorDTO;
import web.mvc.santa_backend.user.dto.UserColorDTO;
import web.mvc.santa_backend.user.entity.*;
import web.mvc.santa_backend.user.repository.ColorRepository;
import web.mvc.santa_backend.user.repository.UserColorRepository;
import web.mvc.santa_backend.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final UserColorRepository userColorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public Page<ColorDTO> getColors(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Colors> colors = colorRepository.findAll(pageable);

        return colors.map(color -> modelMapper.map(color, ColorDTO.class));
    }

    @Override
    public Page<ColorDTO> getColorsByUserId(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Users_Colors> usersColors = userColorRepository.findByUserId(userId, pageable);

        return usersColors.map(uc -> modelMapper.map(uc.getColor(), ColorDTO.class));
    }

    @Override
    public UserColorDTO buyColor(Long userId, Long colorId) {
        Users user = userRepository.findById(userId).orElseThrow(()->new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Colors color = colorRepository.findById(colorId).orElseThrow(()->new NotFoundException(ErrorCode.ITEM_NOT_FOUND));

        // 유저의 point 가 색상의 price 보다 많은지 비교
        if (user.getPoint() < color.getPrice()) throw new InvalidException(ErrorCode.INVALID_BUY);

        Users_Colors uc = Users_Colors.builder()
                .user(user)
                .color(color)
                .createdAt(LocalDateTime.now())
                .build();
        Users_Colors saved = userColorRepository.save(uc);

        return modelMapper.map(saved, UserColorDTO.class);
    }
}
