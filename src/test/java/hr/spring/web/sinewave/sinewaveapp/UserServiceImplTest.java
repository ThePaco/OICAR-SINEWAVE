package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;
import hr.spring.web.sinewave.exception.AuthenticationException;
import hr.spring.web.sinewave.exception.UsernameAlreadyExistsException;
import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import hr.spring.web.sinewave.service.EmailService;
import hr.spring.web.sinewave.service.RefreshTokenService;
import hr.spring.web.sinewave.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private UserCreateDto testUserCreateDto;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUserDto = createTestUserDto();
        testUserCreateDto = createTestUserCreateDto();
    }

    @Test
    void should_CreateUser_When_ValidDataProvided() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(modelMapper.map(testUserCreateDto, User.class)).thenReturn(testUser);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto result = userService.create(testUserCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstname()).isEqualTo("John");
        assertThat(result.getLastname()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).save(any(User.class));
        verify(emailService).sendAccountCreatedEmail("john.doe@example.com", "testuser");
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void should_ThrowUsernameAlreadyExistsException_When_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(testUserCreateDto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("testuser")
                .hasMessageContaining("already taken");

        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendAccountCreatedEmail(any(), any());
    }

    @Test
    void should_AuthenticateUser_When_ValidCredentials() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        testUser.setPasswordsalt("salt123");
        testUser.setPasswordhash("hashedPassword");
        testUser.setIsAnonymized(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123salt123", "hashedPassword")).thenReturn(true);
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        UserDto result = userService.authenticateUser(loginDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123salt123", "hashedPassword");
    }

    @Test
    void should_ThrowAuthenticationException_When_UserNotFound() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("nonexistent");
        loginDto.setPassword("password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticateUser(loginDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void should_ThrowAuthenticationException_When_WrongPassword() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("wrongpassword");

        testUser.setPasswordsalt("salt123");
        testUser.setPasswordhash("hashedPassword");
        testUser.setIsAnonymized(false);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpasswordsalt123", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticateUser(loginDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void should_ThrowAuthenticationException_When_UserIsAnonymized() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password123");

        testUser.setIsAnonymized(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.authenticateUser(loginDto))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Account no longer exists");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void should_ReturnAllUsers_When_GetAll() {
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setIsAnonymized(false);

        UserDto userDto1 = createTestUserDto();
        UserDto userDto2 = createTestUserDto();
        userDto2.setId(2);
        userDto2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(modelMapper.map(user1, UserDto.class)).thenReturn(userDto1);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(userDto2);

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDto::getUsername)
                .containsExactly("testuser", "user2");
    }

    @Test
    void should_FilterAnonymizedUsers_When_GetAll() {
        User normalUser = createTestUser();
        User anonymizedUser = createTestUser();
        anonymizedUser.setId(2);
        anonymizedUser.setIsAnonymized(true);

        UserDto normalUserDto = createTestUserDto();

        when(userRepository.findAll()).thenReturn(Arrays.asList(normalUser, anonymizedUser));
        when(modelMapper.map(normalUser, UserDto.class)).thenReturn(normalUserDto);

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");

        verify(modelMapper, never()).map(anonymizedUser, UserDto.class);
    }

    @Test
    void should_SearchUsersByUsername_When_PartialMatch() {
        String searchTerm = "john";

        User user1 = createTestUser();
        user1.setUsername("john_smith");

        User user2 = createTestUser();
        user2.setId(2);
        user2.setUsername("john_doe");
        user2.setIsAnonymized(false);

        UserDto userDto1 = createTestUserDto();
        userDto1.setUsername("john_smith");

        UserDto userDto2 = createTestUserDto();
        userDto2.setId(2);
        userDto2.setUsername("john_doe");

        when(userRepository.findByUsernameContainingIgnoreCase(searchTerm))
                .thenReturn(Arrays.asList(user1, user2));
        when(modelMapper.map(user1, UserDto.class)).thenReturn(userDto1);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(userDto2);

        List<UserDto> result = userService.searchByUsername(searchTerm);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDto::getUsername)
                .containsExactly("john_smith", "john_doe");
    }

    @Test
    void should_AnonymizeUser_When_UserExists() {
        Integer userId = 1;
        testUser.setIsAnonymized(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.anonymizeUser(userId);

        verify(userRepository).save(argThat(user -> {
            assertThat(user.getIsAnonymized()).isTrue();
            assertThat(user.getAnonymizedAt()).isNotNull();
            assertThat(user.getUsername()).startsWith("anonymous_user_");
            assertThat(user.getFirstname()).isEqualTo("Anonymous");
            assertThat(user.getLastname()).isEqualTo("User");
            assertThat(user.getEmail()).startsWith("deleted_");
            assertThat(user.getPasswordhash()).isEqualTo("ANONYMIZED");
            assertThat(user.getPasswordsalt()).isEqualTo("ANONYMIZED");
            return true;
        }));

        verify(refreshTokenService).deleteByUsername("testuser");
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPasswordhash("hashedPassword");
        user.setPasswordsalt("salt");
        user.setRole(Role.USER);
        user.setIsAnonymized(false);
        return user;
    }

    private UserDto createTestUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1);
        dto.setUsername("testuser");
        dto.setFirstname("John");
        dto.setLastname("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setRole(Role.USER);
        return dto;
    }

    private UserCreateDto createTestUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername("testuser");
        dto.setFirstname("John");
        dto.setLastname("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");
        return dto;
    }
}