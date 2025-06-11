package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.config.JwtUtil;
import hr.spring.web.sinewave.controller.MobileAuthController;
import hr.spring.web.sinewave.dto.AuthResponseDto;
import hr.spring.web.sinewave.dto.RefreshTokenRequestDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.model.RefreshToken;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.RefreshTokenService;
import hr.spring.web.sinewave.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MobileAuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private MobileAuthController mobileAuthController;

    private UserCreateDto userCreateDto;
    private UserLoginDto userLoginDto;
    private UserDto userDto;
    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testuser");
        userCreateDto.setFirstname("Test");
        userCreateDto.setLastname("User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("password");

        userLoginDto = new UserLoginDto();
        userLoginDto.setUsername("testuser");
        userLoginDto.setPassword("password");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setUsername("testuser");
        userDto.setFirstname("Test");
        userDto.setLastname("User");
        userDto.setEmail("test@example.com");
        userDto.setRole(Role.USER);

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);

        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setUserInfo(user);
    }

    @Test
    void testTestConnection_Success() {
        ResponseEntity<Map<String, String>> result = mobileAuthController.testConnection();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("ok", result.getBody().get("status"));
        assertEquals("Mobile API is working", result.getBody().get("message"));
        assertNotNull(result.getBody().get("timestamp"));
    }

    @Test
    void testRegisterUser_Success() {
        when(userService.create(any(UserCreateDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test-jwt-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);

        ResponseEntity<AuthResponseDto> result = mobileAuthController.registerUser(userCreateDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test-jwt-token", result.getBody().getToken());
        assertEquals("refresh-token", result.getBody().getRefreshToken());
        assertEquals("testuser", result.getBody().getUser().getUsername());
    }

    @Test
    void testLoginUser_Success() {
        when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test-jwt-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);

        ResponseEntity<AuthResponseDto> result = mobileAuthController.loginUser(userLoginDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test-jwt-token", result.getBody().getToken());
        assertEquals("refresh-token", result.getBody().getRefreshToken());
        assertEquals("testuser", result.getBody().getUser().getUsername());
    }

    @Test
    void testRefreshToken_Success() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("old-refresh-token");

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("new-refresh-token");
        newRefreshToken.setUserInfo(user);

        when(refreshTokenService.findByToken("old-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("new-jwt-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(newRefreshToken);

        ResponseEntity<AuthResponseDto> result = mobileAuthController.refreshToken(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("new-jwt-token", result.getBody().getToken());
        assertEquals("new-refresh-token", result.getBody().getRefreshToken());
        assertEquals("testuser", result.getBody().getUser().getUsername());
    }

    @Test
    void testRefreshToken_TokenNotFound() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("invalid-token");

        when(refreshTokenService.findByToken("invalid-token")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mobileAuthController.refreshToken(request);
        });

        assertEquals("Refresh Token is not in database!", exception.getMessage());
    }

    @Test
    void testLogout_Success() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        doNothing().when(refreshTokenService).deleteRefreshToken("refresh-token");

        ResponseEntity<Void> result = mobileAuthController.logout(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(refreshTokenService, times(1)).deleteRefreshToken("refresh-token");
    }

    @Test
    void testLogout_Exception() {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        doThrow(new RuntimeException("Token not found")).when(refreshTokenService).deleteRefreshToken("refresh-token");

        ResponseEntity<Void> result = mobileAuthController.logout(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}