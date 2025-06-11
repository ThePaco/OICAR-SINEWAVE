package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.controller.SongController;
import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.SongUpdateDto;
import hr.spring.web.sinewave.exception.NotFoundException;
import hr.spring.web.sinewave.service.SongService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongControllerTest {

    @Mock
    private SongService songService;

    @InjectMocks
    private SongController songController;

    private SongDto songDto;
    private SongCreateDto songCreateDto;
    private SongUpdateDto songUpdateDto;

    @BeforeEach
    void setUp() {
        songDto = new SongDto();
        songDto.setId(1);
        songDto.setTitle("Test Song");
        songDto.setUserId(1);
        songDto.setAlbumId(1);
        songDto.setGenreId(1);
        songDto.setFilepath("test/path/song.mp3");

        songCreateDto = new SongCreateDto();
        songCreateDto.setTitle("New Song");
        songCreateDto.setUserId(1);
        songCreateDto.setAlbumId(1);
        songCreateDto.setGenreId(1);

        songUpdateDto = new SongUpdateDto();
        songUpdateDto.setTitle("Updated Song");
    }

    @Test
    void testGetAllSongs_Success() {
        List<SongDto> songs = Arrays.asList(songDto);
        when(songService.findAll()).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = songController.all();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Test Song", result.getBody().get(0).getTitle());
        verify(songService, times(1)).findAll();
    }

    @Test
    void testFindSongById_Success() {
        when(songService.findById(1)).thenReturn(songDto);

        ResponseEntity<SongDto> result = songController.findById(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Song", result.getBody().getTitle());
        verify(songService, times(1)).findById(1);
    }

    @Test
    void testFindSongById_NotFound() {
        when(songService.findById(999)).thenThrow(new NotFoundException("Song not found"));

        assertThrows(NotFoundException.class, () -> {
            songController.findById(999);
        });
        verify(songService, times(1)).findById(999);
    }

    @Test
    void testFindSongsByAlbumId_Success() {
        List<SongDto> songs = Arrays.asList(songDto);
        when(songService.findByAlbumId(1)).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = songController.findByAlbumId(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(songService, times(1)).findByAlbumId(1);
    }

    @Test
    void testFindSongsByGenreId_Success() {
        List<SongDto> songs = Arrays.asList(songDto);
        when(songService.findByGenreId(1)).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = songController.findByGenreId(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(songService, times(1)).findByGenreId(1);
    }

    @Test
    void testFindSongsByUserId_Success() {
        List<SongDto> songs = Arrays.asList(songDto);
        when(songService.findByUserId(1)).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = songController.findByUserId(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(songService, times(1)).findByUserId(1);
    }

    @Test
    void testSearchSongsByTitle_Success() {
        List<SongDto> songs = Arrays.asList(songDto);
        when(songService.searchByTitle("Test")).thenReturn(songs);

        ResponseEntity<List<SongDto>> result = songController.searchByTitle("Test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        verify(songService, times(1)).searchByTitle("Test");
    }

    @Test
    void testUpdateSong_Success() {
        songDto.setTitle("Updated Song");
        when(songService.update(eq(1), any(SongUpdateDto.class))).thenReturn(songDto);

        ResponseEntity<SongDto> result = songController.update(1, songUpdateDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Updated Song", result.getBody().getTitle());
        verify(songService, times(1)).update(1, songUpdateDto);
    }

    @Test
    void testCreateSongWithUpload_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", "test content".getBytes());
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", "{}".getBytes());

        when(songService.create(any(SongCreateDto.class), any())).thenReturn(songDto);

        ResponseEntity<?> result = songController.createSongWithUpload(file, songCreateDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(songService, times(1)).create(eq(songCreateDto), eq(file));
    }

    @Test
    void testCreateSongWithUpload_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.mp3", "audio/mpeg", new byte[0]);

        ResponseEntity<?> result = songController.createSongWithUpload(emptyFile, songCreateDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof Map);
        Map<String, String> body = (Map<String, String>) result.getBody();
        assertEquals("File cannot be empty.", body.get("message"));
        verify(songService, never()).create(any(), any());
    }

    @Test
    void testDeleteSong_Success() {
        doNothing().when(songService).delete(1);

        ResponseEntity<Void> result = songController.delete(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(songService, times(1)).delete(1);
    }
}

