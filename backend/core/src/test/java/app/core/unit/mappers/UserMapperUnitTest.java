package app.core.unit.mappers;

import app.core.mappers.UserMapper;
import app.core.model.UserEntity;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserMapper Unit Tests")
class UserMapperUnitTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @BeforeEach
    public void init() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Should create user from request with password encoding")
    void shouldCreateUserFromRequestWithPasswordEncoding() {
        // Given
        CreateUserRequestDto request = new CreateUserRequestDto(
                "testuser", "password123", "password123", "Test User", "test@example.com"
        );
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        // When
        UserEntity result = userMapper.createUserFromRequest(request, passwordEncoder);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword123");
        assertThat(result.getDisplayName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getId()).isNull(); // @Mapping(ignore = true)
    }

    @Test
    @DisplayName("Should create user with null display name (use username)")
    void shouldCreateUserWithNullDisplayNameUseUsername() {
        // Given
        CreateUserRequestDto request = new CreateUserRequestDto(
                "testuser", "password123", "password123", null, "test@example.com"
        );
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        UserEntity result = userMapper.createUserFromRequest(request, passwordEncoder);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getDisplayName()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should create user with blank display name (use username)")
    void shouldCreateUserWithBlankDisplayNameUseUsername() {
        // Given
        CreateUserRequestDto request = new CreateUserRequestDto(
                "testuser", "password123", "password123","   ", "test@example.com"
        );
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        UserEntity result = userMapper.createUserFromRequest(request, passwordEncoder);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getDisplayName()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should create user with empty display name (use username)")
    void shouldCreateUserWithEmptyDisplayNameUseUsername() {
        // Given
        CreateUserRequestDto request = new CreateUserRequestDto(
                "testuser", "password123", "password123", "", "test@example.com"
        );
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When
        UserEntity result = userMapper.createUserFromRequest(request, passwordEncoder);

        // Then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getDisplayName()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should update user from request with all fields")
    void shouldUpdateUserFromRequestWithAllFields() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("olduser");
        user.setPassword("oldpassword");
        user.setDisplayName("Old Name");
        user.setEmail("old@example.com");

        UpdateUserRequestDto request = new UpdateUserRequestDto(
                "newpassword", "newpassword", "New Name", "new@example.com"
        );
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        // When
        userMapper.updateUserFromRequest(request, user, passwordEncoder);

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("olduser");
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(user.getDisplayName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should partially update user (ignore null values)")
    void shouldPartiallyUpdateUserIgnoreNullValues() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("oldpassword");
        user.setDisplayName("Old Name");
        user.setEmail("old@example.com");

        UpdateUserRequestDto request = new UpdateUserRequestDto(
                null, null, null, "new@example.com"
        );

        // When
        userMapper.updateUserFromRequest(request, user, passwordEncoder);

        // Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("oldpassword");
        assertThat(user.getDisplayName()).isEqualTo("Old Name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should update user with empty values")
    void shouldUpdateUserWithEmptyValues() {
        // Given
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("oldpassword");
        user.setDisplayName("Old Name");
        user.setEmail("old@example.com");

        UpdateUserRequestDto request = new UpdateUserRequestDto(
                "", "", "", "new@example.com"
        );

        when(passwordEncoder.encode("")).thenReturn("encodedEmptyString");

        // When
        userMapper.updateUserFromRequest(request, user, passwordEncoder);

        // Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("encodedEmptyString");
        assertThat(user.getDisplayName()).isEqualTo("");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should keep old password when new password is null")
    void shouldKeepOldPasswordWhenNewPasswordIsNull() {
        // Given
        UserEntity user = new UserEntity();
        user.setPassword("existingPassword");

        UpdateUserRequestDto request = new UpdateUserRequestDto(
                null, null, "New Name", "new@example.com"
        );

        // When
        userMapper.updateUserFromRequest(request, user, passwordEncoder);

        // Then
        assertThat(user.getPassword()).isEqualTo("existingPassword");
    }

    @Test
    @DisplayName("Should convert user to response")
    void shouldConvertUserToResponse() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setDisplayName("Test User");
        user.setEmail("test@example.com");

        // When
        UserResponseDto result = userMapper.toResponse(user);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.displayName()).isEqualTo("Test User");
        assertThat(result.email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should handle password encoding edge cases")
    void shouldHandlePasswordEncodingEdgeCases() {
        // Given
        CreateUserRequestDto request = new CreateUserRequestDto(
                "testuser", "   ", "   ", "Test User", "test@example.com"
        );
        when(passwordEncoder.encode("   ")).thenReturn("encodedSpaces");

        // When
        UserEntity result = userMapper.createUserFromRequest(request, passwordEncoder);

        // Then
        assertThat(result.getPassword()).isEqualTo("encodedSpaces");
    }
}