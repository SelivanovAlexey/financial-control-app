package app.core.unit.service;

import app.core.model.UserEntity;
import app.core.repository.UserRepository;
import app.core.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
@ActiveProfiles("unit")
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserEntity testUser = new UserEntity(
            1L,
            "testUserDisplayName",
            "testuser",
            "hashedPassword",
            "test@email.com"
    );

    @Order(1)
    @Test
    @DisplayName("Should load user successfully when user exists")
    void shouldLoadUserSuccessfully() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        var result = userService.loadUserByUsername(username);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByUsername(username);
    }

    @Order(2)
    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username 'nonexistentuser' is not found in the system");

        verify(userRepository).findByUsername(username);
    }
}