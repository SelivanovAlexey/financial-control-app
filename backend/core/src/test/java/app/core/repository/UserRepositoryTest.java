package app.core.repository;

import app.core.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@DisplayName("UserRepository Tests")
@ActiveProfiles("integration")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by username when user exists")
    void shouldFindUserByUsernameWhenUserExists() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // When
        Optional<UserEntity> result = userRepository.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getDisplayName()).isEqualTo("Test User");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should return empty optional when user does not exist")
    void shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        // When
        Optional<UserEntity> result = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate username")
    void shouldThrowExceptionWhenCreatingDuplicateUsername() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setDisplayName("User One");
        user1.setUsername("sameuser");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");

        entityManager.persist(user1);
        entityManager.flush();

        UserEntity user2 = new UserEntity();
        user2.setDisplayName("User Two");
        user2.setUsername("sameuser");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }
}