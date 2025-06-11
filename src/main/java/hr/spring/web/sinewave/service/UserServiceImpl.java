package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.exception.AuthenticationException;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.exception.UsernameAlreadyExistsException;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           BCryptPasswordEncoder passwordEncoder, EmailService emailService,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getIsAnonymized())
                .map(this::mapUserToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Integer id) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsAnonymized())
                .map(this::mapUserToUserDto)
                .orElse(null);
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstname(EncryptionUtil.encrypt(dto.getFirstname()));
        user.setLastname(EncryptionUtil.encrypt(dto.getLastname()));
        user.setEmail(EncryptionUtil.encrypt(dto.getEmail()));
        user.setProfilepicture(dto.getProfilepicture());

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);

        String hashedPassword = passwordEncoder.encode(dto.getPassword() + saltStr);

        user.setPasswordsalt(saltStr);
        user.setPasswordhash(hashedPassword);

        User saved = userRepository.save(user);

        emailService.sendAccountCreatedEmail(dto.getEmail(), dto.getUsername());
        return mapUserToUserDto(saved);
    }

    @Override
    public UserDto update(Integer id, UserCreateDto dto) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsAnonymized())
                .map(existing -> {
                    if (!existing.getUsername().equals(dto.getUsername())
                            && userRepository.existsByUsername(dto.getUsername())) {
                        throw new UsernameAlreadyExistsException(dto.getUsername());
                    }

                    modelMapper.map(dto, existing);

                    String hashed = passwordEncoder.encode(dto.getPassword());
                    existing.setPasswordhash(hashed);

                    User updated = userRepository.save(existing);
                    return modelMapper.map(updated, UserDto.class);
                })
                .orElse(null);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto authenticateUser(UserLoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (user.getIsAnonymized()) {
            throw new AuthenticationException("Account no longer exists");
        }

        if (!passwordEncoder.matches(loginDto.getPassword() + user.getPasswordsalt(), user.getPasswordhash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return mapUserToUserDto(user);
    }

    @Override
    public List<UserDto> searchByUsername(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream()
                .filter(user -> !user.getIsAnonymized())
                .map(this::mapUserToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void anonymizeUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getIsAnonymized()) {
            throw new IllegalStateException("User is already anonymized");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String anonymousId = "anonymous_user_" + timestamp;

        String originalUsername = EncryptionUtil.decrypt(user.getUsername());

        user.setUsername(anonymousId);
        user.setFirstname("Anonymous");
        user.setLastname("User");
        user.setEmail("deleted_" + timestamp + "@anonymized.local");
        user.setProfilepicture(null);
        user.setPasswordhash("ANONYMIZED");
        user.setPasswordsalt("ANONYMIZED");
        user.setIsAnonymized(true);
        user.setAnonymizedAt(Instant.now());

        userRepository.save(user);

        try {
            refreshTokenService.deleteByUsername(originalUsername);
        } catch (Exception e) {
            System.err.println("Failed to delete refresh tokens for user: " + originalUsername);
        }
    }

    private UserDto mapUserToUserDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        try {
            dto.setFirstname(EncryptionUtil.decrypt(user.getFirstname()));
            dto.setLastname(EncryptionUtil.decrypt(user.getLastname()));
            dto.setEmail(EncryptionUtil.decrypt(user.getEmail()));
        } catch (RuntimeException e) {
            System.err.println("Decryption error for user ID " + user.getId() + ": " + e.getMessage());
            dto.setUsername("[decryption error]");
        }
        dto.setProfilepicture(user.getProfilepicture());
        dto.setRole(user.getRole());
        return dto;
    }
}