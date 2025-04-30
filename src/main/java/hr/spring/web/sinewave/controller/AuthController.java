package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.config.JwtUtil;
import hr.spring.web.sinewave.dto.AuthResponseDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserCreateDto dto) {
        UserDto registeredUser = userService.create(dto);

        User user = userRepository.findById(registeredUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        String token = jwtUtil.generateToken(user);

        AuthResponseDto response = AuthResponseDto.builder()
                .token(token)
                .user(registeredUser)
                .build();

        return ResponseEntity.created(URI.create("/api/users/" + registeredUser.getId()))
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {
        UserDto authenticatedUser = userService.authenticateUser(userLoginDto);

        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String token = jwtUtil.generateToken(user);

        AuthResponseDto response = AuthResponseDto.builder()
                .token(token)
                .user(authenticatedUser)
                .build();

        return ResponseEntity.ok(response);
    }
}