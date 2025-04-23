package hr.spring.web.sinewave.controller;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserCreateDto dto) {
        UserDto registered = userService.create(dto);

        URI location = URI.create("/api/users/" + registered.getId());
        return ResponseEntity
                .created(location)
                .body(registered);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody UserLoginDto userLoginDto) {
        UserDto authenticatedUser = userService.authenticateUser(userLoginDto);

        return ResponseEntity.ok(authenticatedUser);
    }
}
