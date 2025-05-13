package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.PlaylistCreateDto;
import hr.spring.web.sinewave.dto.PlaylistDto;
import hr.spring.web.sinewave.dto.PlaylistSongDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.exception.UnauthorizedException;
import hr.spring.web.sinewave.model.*;
import hr.spring.web.sinewave.repository.PlaylistRepository;
import hr.spring.web.sinewave.repository.PlaylistSongRepository;
import hr.spring.web.sinewave.repository.SongRepository;
import hr.spring.web.sinewave.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, UserRepository userRepository, SongRepository songRepository, PlaylistSongRepository playlistSongRepository, ModelMapper modelMapper) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.playlistSongRepository = playlistSongRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PlaylistDto> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(playlist -> modelMapper.map(playlist, PlaylistDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDto> getUserPlaylists(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return playlistRepository.findByCreatedby(user).stream()
                .map(playlist -> {
                    PlaylistDto dto = modelMapper.map(playlist, PlaylistDto.class);
                    dto.setSongCount(playlist.getPlaylistsongs().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDto> getPublicPlaylists() {
        return playlistRepository.findByIspublicTrue().stream()
                .map(playlist -> {
                    PlaylistDto dto = modelMapper.map(playlist, PlaylistDto.class);
                    dto.setSongCount(playlist.getPlaylistsongs().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDto getPlaylistById(Integer id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));

        PlaylistDto dto = modelMapper.map(playlist, PlaylistDto.class);
        dto.setSongCount(playlist.getPlaylistsongs().size());
        return dto;
    }

    @Override
    public PlaylistDto createPlaylist(PlaylistCreateDto playlistCreateDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Playlist playlist = new Playlist();
        playlist.setName(playlistCreateDto.getName());
        playlist.setIspublic(playlistCreateDto.getIsPublic());
        playlist.setCreatedby(user);
        playlist.setCreatedat(Instant.now());

        Playlist savedPlaylist = playlistRepository.save(playlist);

        PlaylistDto dto = modelMapper.map(savedPlaylist, PlaylistDto.class);
        dto.setSongCount(0);
        return dto;
    }

    @Override
    public PlaylistDto updatePlaylist(Integer id, PlaylistCreateDto playlistCreateDto, Integer userId) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));

        if (!isUserPlaylistOwner(id, userId)) {
            throw new UnauthorizedException("User is not authorized to update this playlist");
        }

        playlist.setName(playlistCreateDto.getName());
        playlist.setIspublic(playlistCreateDto.getIsPublic());

        Playlist updatedPlaylist = playlistRepository.save(playlist);

        PlaylistDto dto = modelMapper.map(updatedPlaylist, PlaylistDto.class);
        dto.setSongCount(updatedPlaylist.getPlaylistsongs().size());
        return dto;
    }

    @Override
    public void deletePlaylist(Integer id, Integer userId) {
        if (!isUserPlaylistOwner(id, userId)) {
            throw new UnauthorizedException("User is not authorized to delete this playlist");
        }

        playlistRepository.deleteById(id);
    }

    @Override
    public boolean isUserPlaylistOwner(Integer playlistId, Integer userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        return playlist.getCreatedby().getId().equals(userId);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(PlaylistSongDto playlistSongDto, Integer userId) {
        Playlist playlist = playlistRepository.findById(playlistSongDto.getPlaylistId())
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistSongDto.getPlaylistId()));

        if (!isUserPlaylistOwner(playlistSongDto.getPlaylistId(), userId)) {
            throw new UnauthorizedException("User is not authorized to add songs to this playlist");
        }

        Song song = songRepository.findById(playlistSongDto.getSongId())
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id: " + playlistSongDto.getSongId()));

        boolean songExists = playlistSongRepository.existsByPlaylistid_IdAndSongid_Id(
                playlistSongDto.getPlaylistId(),
                playlistSongDto.getSongId()
        );

        if (songExists) {
            return;
        }

        Playlistsong playlistSong = new Playlistsong();
        PlaylistsongId id = new PlaylistsongId();
        id.setPlaylistid(playlistSongDto.getPlaylistId());
        id.setSongid(playlistSongDto.getSongId());

        playlistSong.setId(id);
        playlistSong.setPlaylistid(playlist);
        playlistSong.setSongid(song);
        playlistSong.setAddedat(Instant.now());

        playlistSongRepository.save(playlistSong);
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(PlaylistSongDto playlistSongDto, Integer userId) {
        if (!playlistRepository.existsById(playlistSongDto.getPlaylistId())) {
            throw new ResourceNotFoundException("Playlist not found with id: " + playlistSongDto.getPlaylistId());
        }

        if (!isUserPlaylistOwner(playlistSongDto.getPlaylistId(), userId)) {
            throw new UnauthorizedException("User is not authorized to remove songs from this playlist");
        }

        playlistSongRepository.deleteByPlaylistid_IdAndSongid_Id(
                playlistSongDto.getPlaylistId(),
                playlistSongDto.getSongId()
        );
    }

    @Override
    public List<SongDto> getPlaylistSongs(Integer playlistId, Integer userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        if (!playlist.getIspublic() && !isUserPlaylistOwner(playlistId, userId)) {
            throw new UnauthorizedException("User is not authorized to view songs in this private playlist");
        }

        List<Playlistsong> playlistSongs = playlistSongRepository.findByPlaylistid_Id(playlistId);

        return playlistSongs.stream()
                .map(ps -> modelMapper.map(ps.getSongid(), SongDto.class))
                .collect(Collectors.toList());
    }
}