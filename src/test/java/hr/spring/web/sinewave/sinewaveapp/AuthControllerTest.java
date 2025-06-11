package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.config.JwtUtil;
import hr.spring.web.sinewave.controller.AuthController;
import hr.spring.web.sinewave.dto.AuthResponseDto;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.model.RefreshToken;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.RefreshTokenService;
import hr.spring.web.sinewave.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

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
    void testRegisterUser_Success() {
        when(userService.create(any(UserCreateDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test-jwt-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);

        ResponseEntity<AuthResponseDto> result = authController.registerUser(userCreateDto, response);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test-jwt-token", result.getBody().getToken());
        assertEquals("testuser", result.getBody().getUser().getUsername());
    }

    @Test
    void testLoginUser_Success() {
        when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("test-jwt-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);

        ResponseEntity<AuthResponseDto> result = authController.loginUser(userLoginDto, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("test-jwt-token", result.getBody().getToken());
        assertEquals("testuser", result.getBody().getUser().getUsername());
    }

    @Test
    void testRegisterUser_UserNotFoundAfterRegistration() {
        when(userService.create(any(UserCreateDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.registerUser(userCreateDto, response);
        });

        assertEquals("User not found after registration", exception.getMessage());
    }

    @Test
    void testLoginUser_UserNotFoundAfterAuthentication() {
        when(userService.authenticateUser(any(UserLoginDto.class))).thenReturn(userDto);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.loginUser(userLoginDto, response);
        });

        assertEquals("User not found after authentication", exception.getMessage());
    }
}