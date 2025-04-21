package controller;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationData) {
        try {
            String username = registrationData.get("username");
            String firstname = registrationData.get("firstName");
            String lastname = registrationData.get("lastName");
            String email = registrationData.get("email");
            String password = registrationData.get("password");

            if (username == null || firstname == null || lastname == null || email == null || password == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            User registeredUser = userService.registerUser(username, firstname, lastname, email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("id", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            response.put("message", "User registered successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            User authenticatedUser = userService.authenticateUser(username, password);

            Map<String, Object> response = new HashMap<>();
            response.put("id", authenticatedUser.getId());
            response.put("username", authenticatedUser.getUsername());
            response.put("firstname", authenticatedUser.getFirstname());
            response.put("lastname", authenticatedUser.getLastname());
            response.put("email", authenticatedUser.getEmail());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
