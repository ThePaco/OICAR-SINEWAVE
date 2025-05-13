package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.SongCreateDto;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.SongUpdateDto;
import hr.spring.web.sinewave.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<SongDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody SongUpdateDto dto
    ) {
        SongDto updated = songService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<SongDto> create(@Valid  @RequestBody SongCreateDto dto) {
        SongDto newSong = songService.create(dto);
        return  ResponseEntity.ok(newSong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        songService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
