package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.PlaylistCreateDto;
import hr.spring.web.sinewave.dto.PlaylistDto;
import hr.spring.web.sinewave.dto.PlaylistSongDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "http://localhost:3000")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final UserRepository userRepository;

    @Autowired
    public PlaylistController(PlaylistService playlistService, UserRepository userRepository) {
        this.playlistService = playlistService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (auth.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<PlaylistDto>> getAllPlaylists() {
        List<PlaylistDto> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PlaylistDto>> getUserPlaylists() {
        User currentUser = getCurrentUser();
        List<PlaylistDto> playlists = playlistService.getUserPlaylists(currentUser.getId());
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/public")
    public ResponseEntity<List<PlaylistDto>> getPublicPlaylists() {
        List<PlaylistDto> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable Integer id) {
        PlaylistDto playlist = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(@Valid @RequestBody PlaylistCreateDto playlistCreateDto) {
        User currentUser = getCurrentUser();
        PlaylistDto createdPlaylist = playlistService.createPlaylist(playlistCreateDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> updatePlaylist(
            @PathVariable Integer id,
            @Valid @RequestBody PlaylistCreateDto playlistCreateDto) {

        User currentUser = getCurrentUser();
        PlaylistDto updatedPlaylist = playlistService.updatePlaylist(id, playlistCreateDto, currentUser.getId());
        return ResponseEntity.ok(updatedPlaylist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        playlistService.deletePlaylist(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongDto>> getPlaylistSongs(@PathVariable Integer id) {
        User currentUser = getCurrentUser();
        List<SongDto> songs = playlistService.getPlaylistSongs(id, currentUser.getId());
        return ResponseEntity.ok(songs);
    }

    @PostMapping("/songs")
    public ResponseEntity<Void> addSongToPlaylist(@Valid @RequestBody PlaylistSongDto playlistSongDto) {
        User currentUser = getCurrentUser();
        playlistService.addSongToPlaylist(playlistSongDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/songs")
    public ResponseEntity<Void> removeSongFromPlaylist(@Valid @RequestBody PlaylistSongDto playlistSongDto) {
        User currentUser = getCurrentUser();
        playlistService.removeSongFromPlaylist(playlistSongDto, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}