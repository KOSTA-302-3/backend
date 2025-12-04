package web.mvc.santa_backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;
import web.mvc.santa_backend.user.repository.CustomRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomServiceImpl implements CustomService {

    private final CustomRepository customRepository;
    private final ModelMapper modelMapper;

    @Override
    public CustomDTO getCustomById(Long id) {
        Customs customs = customRepository.findById(id)
                .orElseThrow(()->new RuntimeException("유저 프로필 꾸미기 불러오기 실패"));

        return modelMapper.map(customs, CustomDTO.class);
    }

    @Override
    public Customs addCustom(Users user) {
        Customs custom = Customs.builder()
                .user(user)
                // TODO: Add Default Badge or Default Color
                .build();
        customRepository.save(custom);

        return custom;
    }

    @Override
    public CustomDTO updateCustom() {
        // TODO
        return null;
    }
}
