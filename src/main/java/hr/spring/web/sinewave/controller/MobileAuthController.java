package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.config.JwtUtil;
import hr.spring.web.sinewave.dto.AuthResponseDto;
import hr.spring.web.sinewave.dto.RefreshTokenRequestDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.model.RefreshToken;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.RefreshTokenService;
import hr.spring.web.sinewave.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile/auth")
@CrossOrigin(origins = "*")
public class MobileAuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public MobileAuthController(UserService userService, JwtUtil jwtUtil,
                                UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Mobile API is working");
        response.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserCreateDto dto) {
        UserDto registeredUser = userService.create(dto);

        User user = userRepository.findById(registeredUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        AuthResponseDto response = AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
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

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        AuthResponseDto response = AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(authenticatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(user -> {
                    String accessToken = jwtUtil.generateToken(user);

                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());

                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setUsername(user.getUsername());
                    userDto.setFirstname(user.getFirstname());
                    userDto.setLastname(user.getLastname());
                    userDto.setEmail(user.getEmail());

                    return ResponseEntity.ok(AuthResponseDto.builder()
                            .token(accessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .user(userDto)
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            refreshTokenService.deleteRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}