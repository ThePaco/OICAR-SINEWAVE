package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.config.JwtUtil;
import hr.spring.web.sinewave.dto.AuthResponseDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.model.RefreshToken;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.RefreshTokenService;
import hr.spring.web.sinewave.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil,
                          UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserCreateDto dto,
                                                        HttpServletResponse response) {
        UserDto registeredUser = userService.create(dto);

        User user = userRepository.findById(registeredUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after registration"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        setRefreshTokenCookie(response, refreshToken.getToken());

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(null)
                .user(registeredUser)
                .build();

        return ResponseEntity.created(URI.create("/api/users/" + registeredUser.getId()))
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto,
                                                     HttpServletResponse response) {
        UserDto authenticatedUser = userService.authenticateUser(userLoginDto);

        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        setRefreshTokenCookie(response, refreshToken.getToken());

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(null)
                .user(authenticatedUser)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request,
                                                        HttpServletResponse response) {
        String refreshTokenValue = getRefreshTokenFromCookie(request);

        if (refreshTokenValue == null) {
            return ResponseEntity.badRequest().build();
        }

        return refreshTokenService.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(user -> {
                    String accessToken = jwtUtil.generateToken(user);

                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());
                    setRefreshTokenCookie(response, newRefreshToken.getToken());

                    UserDto userDto = new UserDto();
                    userDto.setId(user.getId());
                    userDto.setUsername(user.getUsername());
                    userDto.setFirstname(user.getFirstname());
                    userDto.setLastname(user.getLastname());
                    userDto.setEmail(user.getEmail());
                    userDto.setRole(user.getRole());

                    return ResponseEntity.ok(AuthResponseDto.builder()
                            .token(accessToken)
                            .refreshToken(null)
                            .user(userDto)
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshTokenValue = getRefreshTokenFromCookie(request);

            if (refreshTokenValue != null) {
                refreshTokenService.deleteRefreshToken(refreshTokenValue);
            }

            clearRefreshTokenCookie(response);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addHeader("Set-Cookie",
                String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict",
                        cookie.getName(), cookie.getValue(), cookie.getMaxAge()));
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}