package hr.spring.web.sinewave.sinewaveapp;

import hr.spring.web.sinewave.model.Role;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_FindUserByUsername_When_UserExists() {
        User user = createAndSaveUser("testuser", "John", "Doe");

        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getFirstname()).isEqualTo("John");
        assertThat(found.get().getLastname()).isEqualTo("Doe");
    }

    @Test
    void should_ReturnEmpty_When_UserNotExists() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void should_ReturnTrue_When_UsernameExists() {
        createAndSaveUser("existinguser", "Jane", "Smith");

        boolean exists = userRepository.existsByUsername("existinguser");

        assertThat(exists).isTrue();
    }

    @Test
    void should_ReturnFalse_When_UsernameNotExists() {
        boolean exists = userRepository.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void should_FindUsersByUsernameContaining_When_PartialMatch() {
        createAndSaveUser("john_smith", "John", "Smith");
        createAndSaveUser("john_doe", "John", "Doe");
        createAndSaveUser("jane_doe", "Jane", "Doe");
        createAndSaveUser("bob_johnson", "Bob", "Johnson");

        List<User> found = userRepository.findByUsernameContainingIgnoreCase("john");

        assertThat(found).hasSize(3); // john_smith, john_doe, bob_johnson
        assertThat(found).extracting(User::getUsername)
                .containsExactlyInAnyOrder("john_smith", "john_doe", "bob_johnson");
    }

    @Test
    void should_FindUsersByUsernameContaining_When_CaseInsensitive() {
        createAndSaveUser("JohnSmith", "John", "Smith");
        createAndSaveUser("johnDoe", "John", "Doe");

        List<User> found = userRepository.findByUsernameContainingIgnoreCase("john");

        assertThat(found).hasSize(2);
        assertThat(found).extracting(User::getUsername)
                .containsExactlyInAnyOrder("JohnSmith", "johnDoe");
    }

    @Test
    void should_ReturnEmptyList_When_NoUsernameMatches() {
        createAndSaveUser("alice", "Alice", "Wonder");
        createAndSaveUser("bob", "Bob", "Builder");

        List<User> found = userRepository.findByUsernameContainingIgnoreCase("charlie");

        assertThat(found).isEmpty();
    }

    @Test
    void should_SaveAndRetrieveUser_When_AllFieldsProvided() {
        // Given
        User user = new User();
        user.setUsername("completeuser");
        user.setFirstname("Complete");
        user.setLastname("User");
        user.setEmail("complete@example.com");
        user.setPasswordhash("hashedPassword");
        user.setPasswordsalt("salt123");
        user.setRole(Role.USER);
        user.setIsAnonymized(false);
        user.setProfilepicture("http://example.com/pic.jpg");

        User saved = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        User retrieved = userRepository.findById(saved.getId()).orElse(null);

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getUsername()).isEqualTo("completeuser");
        assertThat(retrieved.getFirstname()).isEqualTo("Complete");
        assertThat(retrieved.getLastname()).isEqualTo("User");
        assertThat(retrieved.getEmail()).isEqualTo("complete@example.com");
        assertThat(retrieved.getPasswordhash()).isEqualTo("hashedPassword");
        assertThat(retrieved.getPasswordsalt()).isEqualTo("salt123");
        assertThat(retrieved.getRole()).isEqualTo(Role.USER);
        assertThat(retrieved.getIsAnonymized()).isFalse();
        assertThat(retrieved.getProfilepicture()).isEqualTo("http://example.com/pic.jpg");
    }

    @Test
    void should_SaveUserWithAdminRole_When_RoleSet() {
        // Given
        User admin = createUser("admin", "Admin", "User");
        admin.setRole(Role.ADMIN);

        User saved = userRepository.save(admin);
        entityManager.flush();

        User retrieved = userRepository.findById(saved.getId()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void should_SaveUserWithDefaultRole_When_RoleNotSet() {
        User user = createUser("regularuser", "Regular", "User");

        User saved = userRepository.save(user);
        entityManager.flush();

        User retrieved = userRepository.findById(saved.getId()).orElse(null);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getRole()).isEqualTo(Role.USER);
    }

    private User createAndSaveUser(String username, String firstname, String lastname) {
        User user = createUser(username, firstname, lastname);
        return entityManager.persistAndFlush(user);
    }

    private User createUser(String username, String firstname, String lastname) {
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(username + "@example.com");
        user.setPasswordhash("hashedPassword");
        user.setPasswordsalt("salt");
        user.setRole(Role.USER);
        user.setIsAnonymized(false);
        return user;
    }
}
