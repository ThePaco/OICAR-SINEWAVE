package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.UserFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/friends")
@CrossOrigin(origins = "http://localhost:3000")
public class UserFriendController {

    private final UserFriendService userFriendService;
    private final UserRepository userRepository;

    @Autowired
    public UserFriendController(UserFriendService userFriendService, UserRepository userRepository) {
        this.userFriendService = userFriendService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
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

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<Map<String, String>> followUser(@PathVariable Integer targetUserId) {
        User currentUser = getCurrentUser();
        userFriendService.followUser(currentUser.getId(), targetUserId);
        return ResponseEntity.ok(Map.of("message", "You are now following this user"));
    }

    @DeleteMapping("/unfollow/{targetUserId}")
    public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable Integer targetUserId) {
        User currentUser = getCurrentUser();
        userFriendService.unfollowUser(currentUser.getId(), targetUserId);
        return ResponseEntity.ok(Map.of("message", "You have unfollowed this user"));
    }

    @GetMapping("/following")
    public ResponseEntity<List<UserDto>> getFollowing() {
        User currentUser = getCurrentUser();
        List<UserDto> following = userFriendService.getFollowing(currentUser.getId());
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserDto>> getFollowers() {
        User currentUser = getCurrentUser();
        List<UserDto> followers = userFriendService.getFollowers(currentUser.getId());
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDto>> getUserFollowing(@PathVariable Integer userId) {
        List<UserDto> following = userFriendService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getUserFollowers(@PathVariable Integer userId) {
        List<UserDto> followers = userFriendService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/is-following/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable Integer targetUserId) {
        User currentUser = getCurrentUser();
        boolean isFollowing = userFriendService.isFollowing(currentUser.getId(), targetUserId);
        return ResponseEntity.ok(Map.of("following", isFollowing));
    }
}