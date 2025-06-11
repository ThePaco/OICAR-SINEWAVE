package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.controller.PlaylistController;
import hr.spring.web.sinewave.dto.PlaylistCreateDto;
import hr.spring.web.sinewave.dto.PlaylistDto;
import hr.spring.web.sinewave.dto.PlaylistSongDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.PlaylistService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTest {

    @Mock
    private PlaylistService playlistService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PlaylistController playlistController;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;
    private PlaylistDto playlistDto;
    private PlaylistCreateDto playlistCreateDto;
    private PlaylistSongDto playlistSongDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setRole(Role.USER);

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setUsername("testuser");
        userDto.setRole(Role.USER);

        playlistDto = new PlaylistDto();
        playlistDto.setId(1);
        playlistDto.setName("Test Playlist");
        playlistDto.setCreatedBy(userDto);
        playlistDto.setCreatedAt(Instant.now());
        playlistDto.setIsPublic(false);
        playlistDto.setSongCount(0);

        playlistCreateDto = new PlaylistCreateDto();
        playlistCreateDto.setName("New Playlist");
        playlistCreateDto.setIsPublic(false);

        playlistSongDto = new PlaylistSongDto();
        playlistSongDto.setPlaylistId(1);
        playlistSongDto.setSongId(1);

        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    private void setupSecurityContext() {
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void testGetAllPlaylists_Success() {
        List<PlaylistDto> playlists = Arrays.asList(playlistDto);
        when(playlistService.getAllPlaylists()).thenReturn(playlists);

        ResponseEntity<List<PlaylistDto>> result = playlistController.getAllPlaylists();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Test Playlist", result.getBody().get(0).getName());
        verify(playlistService, times(1)).getAllPlaylists();
    }

    @Test
    void testGetUserPlaylists_Success() {
        setupSecurityContext();
        List<PlaylistDto> playlists = Arrays.asList(playlistDto);
        when(playlistService.getUserPlaylists(1)).thenReturn(playlists);

        ResponseEntity<List<PlaylistDto>> result = playlistController.getUserPlaylists();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(playlistService, times(1)).getUserPlaylists(1);
    }

    @Test
    void testGetPublicPlaylists_Success() {
        playlistDto.setIsPublic(true);
        List<PlaylistDto> playlists = Arrays.asList(playlistDto);
        when(playlistService.getPublicPlaylists()).thenReturn(playlists);

        ResponseEntity<List<PlaylistDto>> result = playlistController.getPublicPlaylists();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertTrue(result.getBody().get(0).getIsPublic());
        verify(playlistService, times(1)).getPublicPlaylists();
    }

    @Test
    void testGetPlaylistById_Success() {
        when(playlistService.getPlaylistById(1)).thenReturn(playlistDto);

        ResponseEntity<PlaylistDto> result = playlistController.getPlaylistById(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Playlist", result.getBody().getName());
        verify(playlistService, times(1)).getPlaylistById(1);
    }

    @Test
    void testCreatePlaylist_Success() {
        setupSecurityContext();
        when(playlistService.createPlaylist(any(PlaylistCreateDto.class), eq(1))).thenReturn(playlistDto);

        ResponseEntity<PlaylistDto> result = playlistController.createPlaylist(playlistCreateDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Playlist", result.getBody().getName());
        verify(playlistService, times(1)).createPlaylist(playlistCreateDto, 1);
    }

    @Test
    void testUpdatePlaylist_Success() {
        setupSecurityContext();
        PlaylistDto updatedPlaylistDto = new PlaylistDto();
        updatedPlaylistDto.setId(1);
        updatedPlaylistDto.setName("Updated Playlist");
        updatedPlaylistDto.setCreatedBy(userDto);
        updatedPlaylistDto.setCreatedAt(Instant.now());
        updatedPlaylistDto.setIsPublic(false);
        updatedPlaylistDto.setSongCount(0);

        when(playlistService.updatePlaylist(eq(1), any(PlaylistCreateDto.class), eq(1))).thenReturn(updatedPlaylistDto);

        ResponseEntity<PlaylistDto> result = playlistController.updatePlaylist(1, playlistCreateDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Updated Playlist", result.getBody().getName());
    }

    @Test
    void testDeletePlaylist_Success() {
        setupSecurityContext();
        doNothing().when(playlistService).deletePlaylist(1, 1);

        ResponseEntity<Void> result = playlistController.deletePlaylist(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testAddSongToPlaylist_Success() {
        setupSecurityContext();
        doNothing().when(playlistService).addSongToPlaylist(any(PlaylistSongDto.class), eq(1));

        ResponseEntity<Void> result = playlistController.addSongToPlaylist(playlistSongDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testRemoveSongFromPlaylist_Success() {
        setupSecurityContext();
        doNothing().when(playlistService).removeSongFromPlaylist(any(PlaylistSongDto.class), eq(1));

        ResponseEntity<Void> result = playlistController.removeSongFromPlaylist(playlistSongDto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testGetPlaylistSongs_Success() {
        setupSecurityContext();
        SongDto songDto = new SongDto();
        songDto.setId(1);
        songDto.setTitle("Test Song");
        List<SongDto> songs = Arrays.asList(songDto);

        when(playlistService.getPlaylistSongs(1, 1)).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = playlistController.getPlaylistSongs(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Test Song", result.getBody().get(0).getTitle());
    }
}