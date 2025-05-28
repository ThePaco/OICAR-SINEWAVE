package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.exception.UnauthorizedException;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.SongService;
import hr.spring.web.sinewave.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserService userService;
    private final SongService songService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(UserService userService, SongService songService, UserRepository userRepository) {
        this.userService = userService;
        this.songService = songService;
        this.userRepository = userRepository;
    }

    private void checkAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (auth.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName();
        }

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Access denied. Admin role required.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        checkAdminRole();
        List<UserDto> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/songs")
    public ResponseEntity<List<SongDto>> getAllSongs() {
        checkAdminRole();
        List<SongDto> songs = songService.findAll();
        return ResponseEntity.ok(songs);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        checkAdminRole();

        // Ne dozvoljavamo brisanje sebe
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getId().equals(id)) {
            throw new UnauthorizedException("Cannot delete your own account");
        }

        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Integer id) {
        checkAdminRole();
        songService.delete(id);
        return ResponseEntity.noContent().build();
    }
}