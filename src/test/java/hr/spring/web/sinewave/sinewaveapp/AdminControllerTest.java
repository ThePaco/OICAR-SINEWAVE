package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.controller.AdminController;
import hr.spring.web.sinewave.dto.SongDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.exception.UnauthorizedException;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.SongService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SongService songService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    private User adminUser;
    private User regularUser;
    private UserDto userDto;
    private SongDto songDto;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2);
        regularUser.setUsername("user");
        regularUser.setRole(Role.USER);

        userDto = new UserDto();
        userDto.setId(2);
        userDto.setUsername("user");
        userDto.setRole(Role.USER);

        songDto = new SongDto();
        songDto.setId(1);
        songDto.setTitle("Test Song");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllUsers_AdminSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userService.getAll()).thenReturn(Arrays.asList(userDto));

        ResponseEntity<List<UserDto>> result = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testGetAllUsers_UserForbidden() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(regularUser));

        assertThrows(UnauthorizedException.class, () -> {
            adminController.getAllUsers();
        });
    }

    @Test
    void testGetAllSongs_AdminSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(songService.findAll()).thenReturn(Arrays.asList(songDto));

        ResponseEntity<List<SongDto>> result = adminController.getAllSongs();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testDeleteUser_AdminSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        doNothing().when(userService).delete(2);

        ResponseEntity<Void> result = adminController.deleteUser(2);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void testDeleteUser_CannotDeleteSelf() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        assertThrows(UnauthorizedException.class, () -> {
            adminController.deleteUser(1);
        });
    }

    @Test
    void testDeleteSong_AdminSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        doNothing().when(songService).delete(1);

        ResponseEntity<Void> result = adminController.deleteSong(1);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}