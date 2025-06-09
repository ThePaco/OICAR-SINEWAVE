package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.SongUpdateDto;
import hr.spring.web.sinewave.exception.NotFoundException;
import hr.spring.web.sinewave.service.SongService;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@Validated
public class SongController {
    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<List<SongDto>> all() {
        return ResponseEntity.ok(songService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> findById(@PathVariable Integer id) {
        return  ResponseEntity.ok(songService.findById(id));
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<List<SongDto>> findByAlbumId(@PathVariable Integer id) {
        return  ResponseEntity.ok(songService.findByAlbumId(id));
    }

    @GetMapping("/genre/{id}")
    public ResponseEntity<List<SongDto>> findByGenreId(@PathVariable Integer id) {
        return  ResponseEntity.ok(songService.findByGenreId(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SongDto>> findByUserId(@PathVariable Integer id) {
        return  ResponseEntity.ok(songService.findByUserId(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDto>> searchByTitle(@RequestParam("title") String title) {
        List<SongDto> songs = songService.searchByTitle(title);
        return ResponseEntity.ok(songs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SongDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody SongUpdateDto dto
    )
    {
        SongDto updated = songService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stream/{id}") // Changed to path variable for ID
    public ResponseEntity<InputStreamResource> streamMusicById(@PathVariable Integer id) throws IOException {
        SongDto songDto = songService.findById(id);

        if (songDto == null || songDto.getFilepath() == null || songDto.getFilepath().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        File file = new File(songDto.getFilepath());

        if (!file.exists() || !file.canRead()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        InputStream inputStream = new FileInputStream(file);
        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setContentLength(file.length());
        headers.set("Accept-Ranges", "bytes");


        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createSongWithUpload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") @Valid SongCreateDto metadataDto
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "File cannot be empty."));
            }
            SongDto newSong = songService.create(metadataDto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSong);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (NotFoundException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An internal error occurred while processing the song."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        songService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
