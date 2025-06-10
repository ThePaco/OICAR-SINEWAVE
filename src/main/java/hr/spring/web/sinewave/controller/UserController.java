package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        UserDto user = userService.getById(id);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            User currentUser = getCurrentUserFromContext();
            UserDto userDto = userService.getById(currentUser.getId());
            return ResponseEntity.ok(userDto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchByUsername(@RequestParam("username") String username) {
        List<UserDto> users = userService.searchByUsername(username);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid  @RequestBody UserCreateDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id, @Valid @RequestBody UserCreateDto dto) {
        UserDto updated = userService.update(id, dto);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/anonymize-me")
    public ResponseEntity<Map<String, String>> anonymizeCurrentUser() {
        try {
            User currentUser = getCurrentUserFromContext();

            if (currentUser.getIsAnonymized()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Account is already anonymized"));
            }

            userService.anonymizeUser(currentUser.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Your account has been successfully anonymized. You will be logged out shortly.",
                    "status", "success"
            ));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "An error occurred while processing your request"));
        }
    }

    private User getCurrentUserFromContext() {
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
}