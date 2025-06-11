package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.controller.UserController;
import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;
    private UserCreateDto userCreateDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1);
        userDto.setUsername("testuser");
        userDto.setFirstname("Test");
        userDto.setLastname("User");
        userDto.setEmail("test@example.com");
        userDto.setRole(Role.USER);

        userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testuser");
        userCreateDto.setFirstname("Test");
        userCreateDto.setLastname("User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("password");

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setIsAnonymized(false);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllUsers_Success() {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.getAll()).thenReturn(users);

        List<UserDto> result = userController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetUserById_Success() {
        when(userService.getById(1)).thenReturn(userDto);

        ResponseEntity<UserDto> result = userController.getUserById(1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
        verify(userService, times(1)).getById(1);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userService.getById(999)).thenReturn(null);

        ResponseEntity<UserDto> result = userController.getUserById(999);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(userService, times(1)).getById(999);
    }

    @Test
    void testGetCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.getById(1)).thenReturn(userDto);

        ResponseEntity<UserDto> result = userController.getCurrentUser();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
    }

    @Test
    void testGetCurrentUser_NotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("nonexistent");
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        ResponseEntity<UserDto> result = userController.getCurrentUser();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testSearchByUsername_Success() {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.searchByUsername("test")).thenReturn(users);

        ResponseEntity<List<UserDto>> result = userController.searchByUsername("test");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("testuser", result.getBody().get(0).getUsername());
        verify(userService, times(1)).searchByUsername("test");
    }

    @Test
    void testCreateUser_Success() {
        when(userService.create(any(UserCreateDto.class))).thenReturn(userDto);

        ResponseEntity<UserDto> result = userController.createUser(userCreateDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
        verify(userService, times(1)).create(userCreateDto);
    }

    @Test
    void testUpdateUser_Success() {
        when(userService.update(eq(1), any(UserCreateDto.class))).thenReturn(userDto);

        ResponseEntity<UserDto> result = userController.updateUser(1, userCreateDto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
        verify(userService, times(1)).update(1, userCreateDto);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userService.update(eq(999), any(UserCreateDto.class))).thenReturn(null);

        ResponseEntity<UserDto> result = userController.updateUser(999, userCreateDto);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(userService, times(1)).update(999, userCreateDto);
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).delete(1);

        ResponseEntity<Void> result = userController.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userService, times(1)).delete(1);
    }

    @Test
    void testAnonymizeCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userService).anonymizeUser(1);

        ResponseEntity<Map<String, String>> result = userController.anonymizeCurrentUser();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("success", result.getBody().get("status"));
        verify(userService, times(1)).anonymizeUser(1);
    }

    @Test
    void testAnonymizeCurrentUser_AlreadyAnonymized() {
        user.setIsAnonymized(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> result = userController.anonymizeCurrentUser();

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Account is already anonymized", result.getBody().get("message"));
        verify(userService, never()).anonymizeUser(anyInt());
    }
}

