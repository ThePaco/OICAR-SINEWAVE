package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.controller.UserFriendController;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.UserFriendService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFriendControllerTest {

    @Mock
    private UserFriendService userFriendService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserFriendController userFriendController;

    private User currentUser;
    private UserDto friendDto;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setUsername("testuser");
        currentUser.setRole(Role.USER);

        friendDto = new UserDto();
        friendDto.setId(2);
        friendDto.setUsername("friend");
        friendDto.setRole(Role.USER);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testFollowUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        doNothing().when(userFriendService).followUser(1, 2);

        ResponseEntity<Map<String, String>> result = userFriendController.followUser(2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("You are now following this user", result.getBody().get("message"));
    }

    @Test
    void testUnfollowUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        doNothing().when(userFriendService).unfollowUser(1, 2);

        ResponseEntity<Map<String, String>> result = userFriendController.unfollowUser(2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("You have unfollowed this user", result.getBody().get("message"));
    }

    @Test
    void testGetFollowing_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(userFriendService.getFollowing(1)).thenReturn(Arrays.asList(friendDto));

        ResponseEntity<List<UserDto>> result = userFriendController.getFollowing();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("friend", result.getBody().get(0).getUsername());
    }

    @Test
    void testGetFollowers_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(userFriendService.getFollowers(1)).thenReturn(Arrays.asList(friendDto));

        ResponseEntity<List<UserDto>> result = userFriendController.getFollowers();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testGetUserFollowing_Success() {
        when(userFriendService.getFollowing(2)).thenReturn(Arrays.asList(friendDto));

        ResponseEntity<List<UserDto>> result = userFriendController.getUserFollowing(2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testGetUserFollowers_Success() {
        when(userFriendService.getFollowers(2)).thenReturn(Arrays.asList(friendDto));

        ResponseEntity<List<UserDto>> result = userFriendController.getUserFollowers(2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void testIsFollowing_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(userFriendService.isFollowing(1, 2)).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> result = userFriendController.isFollowing(2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().get("following"));
    }
}
