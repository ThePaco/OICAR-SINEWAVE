package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.GenreDto;
import java.util.List;

public interface GenreService {
    List<GenreDto> getAllGenres();
}