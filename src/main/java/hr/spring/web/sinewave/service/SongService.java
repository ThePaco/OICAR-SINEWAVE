package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.SongUpdateDto;
import hr.spring.web.sinewave.model.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {
    List<SongDto> findAll();
    SongDto findById(Integer id);
    SongDto create(SongCreateDto dto, MultipartFile file);
    SongDto update(Integer id, SongUpdateDto dto);
    void delete(Integer id);
    List<SongDto> findByUserId(Integer userId);
    List<SongDto> findByAlbumId(Integer albumId);
    List<SongDto> findByGenreId(Integer genreId);
    List<SongDto> searchByTitle(String title);
}
