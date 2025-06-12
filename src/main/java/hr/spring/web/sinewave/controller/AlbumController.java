package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.AlbumDropdownDto; // Or Album model
import hr.spring.web.sinewave.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumDropdownDto>> getAllAlbumsForDropdown() {
        return ResponseEntity.ok(albumService.getAllAlbumsForDropdown());
    }
}