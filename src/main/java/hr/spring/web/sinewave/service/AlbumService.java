package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.AlbumDropdownDto;
import java.util.List;

public interface AlbumService {
    List<AlbumDropdownDto> getAllAlbumsForDropdown();
}