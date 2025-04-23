package hr.spring.web.sinewave.config;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(UserCreateDto.class, User.class)
                .addMappings(m -> m.skip(User::setId));
        return mapper;
    }
}
