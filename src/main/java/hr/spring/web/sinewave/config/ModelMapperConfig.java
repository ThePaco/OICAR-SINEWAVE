package hr.spring.web.sinewave.config;

import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.model.Song;
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

        mapper.typeMap(Song.class, SongDto.class)
                .addMappings(m -> {
                    m.map(src  -> src.getUserid().getId(),
                            SongDto::setUserId);

                    m.map(src  -> src.getAlbumid().getId(),
                            SongDto::setAlbumId);

                    m.map(src  -> src.getGenreid().getId(),
                            SongDto::setGenreId);
                });

        mapper.typeMap(Song.class, SongCreateDto.class)
                .addMappings(m -> {
                    m.map(src  -> src.getUserid().getId(),
                            SongCreateDto::setUserId);

                    m.map(src  -> src.getAlbumid().getId(),
                            SongCreateDto::setAlbumId);

                    m.map(src  -> src.getGenreid().getId(),
                            SongCreateDto::setGenreId);
                });

        return mapper;
    }
}
