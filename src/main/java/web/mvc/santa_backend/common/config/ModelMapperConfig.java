package web.mvc.santa_backend.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import web.mvc.santa_backend.user.dto.CustomDTO;
import web.mvc.santa_backend.user.dto.UserResponseDTO;
import web.mvc.santa_backend.user.dto.UserSimpleDTO;
import web.mvc.santa_backend.user.entity.Customs;
import web.mvc.santa_backend.user.entity.Users;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Users -> UserResponseDTO (Custom->CustomDTO 매핑 설정)
        modelMapper.typeMap(Users.class, UserResponseDTO.class)
                .addMappings(mapper -> mapper.map(Users::getCustom, UserResponseDTO::setCustomDTO));

        // Users -> UsersSimpleTO (Custom->CustomDTO 매핑 설정)
        modelMapper.typeMap(Users.class, UserSimpleDTO.class)
                .addMappings(mapper -> mapper.map(Users::getCustom, UserSimpleDTO::setCustomDTO));

        // Customs -> CustomDTO (BadgeDTO, ColorDTO 매핑)
        modelMapper.typeMap(Customs.class, CustomDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Customs::getBadge, CustomDTO::setBadgeDTO);
                    mapper.map(Customs::getColor, CustomDTO::setColorDTO);
                });

        return modelMapper;
    }


}