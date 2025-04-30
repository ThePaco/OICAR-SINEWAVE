package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.PlaylistCreateDto;
import hr.spring.web.sinewave.dto.PlaylistDto;

import java.util.List;

public interface PlaylistService {
    List<PlaylistDto> getAllPlaylists();
    List<PlaylistDto> getUserPlaylists(Integer userId);
    List<PlaylistDto> getPublicPlaylists();
    PlaylistDto getPlaylistById(Integer id);
    PlaylistDto createPlaylist(PlaylistCreateDto playlistCreateDto, Integer userId);
    PlaylistDto updatePlaylist(Integer id, PlaylistCreateDto playlistCreateDto, Integer userId);
    void deletePlaylist(Integer id, Integer userId);
    boolean isUserPlaylistOwner(Integer playlistId, Integer userId);
}