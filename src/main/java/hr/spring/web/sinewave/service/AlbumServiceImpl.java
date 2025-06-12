package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.AlbumDropdownDto;
import hr.spring.web.sinewave.repository.AlbumRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, ModelMapper modelMapper) {
        this.albumRepository = albumRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AlbumDropdownDto> getAllAlbumsForDropdown() {
        return albumRepository.findAll()
                .stream()
                .map(album -> {
                    AlbumDropdownDto dto = modelMapper.map(album, AlbumDropdownDto.class);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}