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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService{
    private SongRepository songRepository;
    private ModelMapper modelMapper;
    private AlbumRepository albumRepository;
    private GenreRepository genreRepository;
    private UserRepository userRepository;

    public SongServiceImpl(SongRepository songRepository, ModelMapper modelMapper, AlbumRepository albumRepository, GenreRepository genreRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.modelMapper = modelMapper;
        this.albumRepository = albumRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
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
    public SongDto create(SongCreateDto dto) {
        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setDuration(dto.getDuration());
        song.setFilepath(dto.getFilepath());
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
        if (dto.getDuration() != null) existing.setDuration(dto.getDuration());
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
}
