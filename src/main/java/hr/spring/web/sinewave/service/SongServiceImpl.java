package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.SongUpdateDto;
import hr.spring.web.sinewave.exception.NotFoundException;
import hr.spring.web.sinewave.model.Album;
import hr.spring.web.sinewave.model.Genre;
import hr.spring.web.sinewave.model.Song;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.AlbumRepository;
import hr.spring.web.sinewave.repository.GenreRepository;
import hr.spring.web.sinewave.repository.SongRepository;
import hr.spring.web.sinewave.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class SongServiceImpl implements SongService{
    private SongRepository songRepository;
    private ModelMapper modelMapper;
    private AlbumRepository albumRepository;
    private GenreRepository genreRepository;
    private UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public SongServiceImpl(SongRepository songRepository, ModelMapper modelMapper, AlbumRepository albumRepository, GenreRepository genreRepository, UserRepository userRepository, FileUploadService fileUploadService) {
        this.songRepository = songRepository;
        this.modelMapper = modelMapper;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public List<SongDto> findAll() {
        List<Song> songs = songRepository.findAll();
               return songs.stream()
                .map(song -> modelMapper.map(song, SongDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public SongDto findById(Integer id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found"));
        return modelMapper.map(song, SongDto.class);
    }

    @Override
    public SongDto create(SongCreateDto dto, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        String relativePathForDb = "music/" + originalFilename;
        try {
            String filename = file.getOriginalFilename();
            Path path = Paths.get("music/" + filename);
            Files.copy(file.getInputStream(), path, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded file: " + e.getMessage(), e);
        }
        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setFilepath(relativePathForDb);
        song.setCreatedat(Instant.now());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        song.setUserid(user);

        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found"));
            song.setAlbumid(album);
        }

        if (dto.getGenreId() != null) {
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Genre not found"));
            song.setGenreid(genre);
        }

        Song saved = songRepository.save(song);
        return modelMapper.map(saved, SongDto.class);
    }

    @Override
    public SongDto update(Integer id, SongUpdateDto dto) {
        Song existing = songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found"));
            existing.setAlbumid(album);
        }
        if (dto.getGenreId() != null){
            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Genre not found"));
            existing.setGenreid(genre);
        }
        if (dto.getFilepath() != null) existing.setFilepath(dto.getFilepath());

        Song updated = songRepository.save(existing);
        return modelMapper.map(updated, SongDto.class);
    }

    @Override
    public void delete(Integer id) {
        if (!songRepository.existsById(id)) {
            throw new NotFoundException("Song not found");
        }
        songRepository.deleteById(id);
    }

    @Override
    public List<SongDto> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        return songRepository.findByUserid_Id(userId)
                .stream()
                .map(song -> modelMapper.map(song, SongDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDto> findByAlbumId(Integer albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new NotFoundException("Album not found");
        }
        return songRepository.findByAlbumid_Id(albumId)
                .stream()
                .map(song -> modelMapper.map(song, SongDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDto> findByGenreId(Integer genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw new NotFoundException("Genre not found");
        }
        return songRepository.findByGenreid_Id(genreId)
                .stream()
                .map(song -> modelMapper.map(song, SongDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDto> searchByTitle(String title) {
        List<Song> songs = songRepository.findByTitleContainingIgnoreCase(title);
        return songs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private SongDto convertToDto(Song song) {
        SongDto dto = modelMapper.map(song, SongDto.class);

        if (song.getUserid() != null) {
            dto.setArtistName(song.getUserid().getFirstname() + " " + song.getUserid().getLastname());
        }
        if (song.getAlbumid() != null) {
            dto.setAlbumName(song.getAlbumid().getName());
        }
        if (song.getGenreid() != null) {
            dto.setGenreName(song.getGenreid().getName());
        }

        return dto;
    }
}
