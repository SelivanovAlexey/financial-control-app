package app.core.unit.service;

import app.core.mappers.UserMapper;
import app.core.model.UserEntity;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import app.core.repository.UserRepository;
import app.core.service.UserManagementServiceImpl;
import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.unit.utils.TestUtils;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserManagementServiceImpl Unit Tests")
class UserManagementServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private final UserEntity testUser = UserEntity.builder()
            .id(1L)
            .displayName("testUserDisplayName")
            .username("testuser")
            .password("hashedPassword")
            .email("test@email.com")
            .build();

    /* =======================
       CREATE USER
       ======================= */

    @Order(1)
    @Test
    @DisplayName("Should create user successfully when valid request provided")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequestDto request = createUserRequest();

        UserEntity mappedEntity = createUserEntity(null);
        UserEntity savedEntity = createUserEntity(1L);
        UserResponseDto expectedResponse = createUserResponse("testUserDisplayName", "test@email.com");

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userMapper.createUserFromRequest(request, passwordEncoder)).thenReturn(mappedEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.createUser(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(userRepository).existsByUsername(request.username());
        verify(userMapper).createUserFromRequest(request, passwordEncoder);
        verify(userRepository).save(argThat(entity -> {
            assertThat(entity.getUsername()).isEqualTo("testuser");
            assertThat(entity.getDisplayName()).isEqualTo("testUserDisplayName");
            assertThat(entity.getEmail()).isEqualTo("test@email.com");
            return true;
        }));
    }

    @Order(2)
    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user with same username exists")
    void shouldThrowUserAlreadyExistsExceptionWhenUserExists() {
        // Given
        CreateUserRequestDto request = createUserRequest();

        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userManagementService.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'testuser' already exists");

        verify(userRepository).existsByUsername(request.username());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Order(3)
    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user with same username exists (race check)")
    void shouldThrowUserAlreadyExistsExceptionWhenUserExistsRaceCheck() {
        // Given
        CreateUserRequestDto request = createUserRequest();
        UserEntity mappedEntity = createUserEntity(null);

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userMapper.createUserFromRequest(request, passwordEncoder)).thenReturn(mappedEntity);
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("msg", new ConstraintViolationException("msg", null, "uq_users_username")));

        // When & Then
        assertThatThrownBy(() -> userManagementService.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'testuser' already exists");

        verify(userRepository).existsByUsername(request.username());
        verify(userMapper).createUserFromRequest(request, passwordEncoder);
        verify(userRepository).save(any());
        verify(userMapper, never()).toResponse(any());
    }

    @Order(4)
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "other_constraint")
    @DisplayName("Should rethrow DataIntegrityViolationException when constraint is not username")
    void shouldRethrowDataIntegrityViolationExceptionWhenConstraintIsNotUsername(String constraintName) {
        // Given
        CreateUserRequestDto request = createUserRequest();
        UserEntity mappedEntity = createUserEntity(null);

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userMapper.createUserFromRequest(request, passwordEncoder)).thenReturn(mappedEntity);
        Exception ex = new DataIntegrityViolationException("msg", new ConstraintViolationException("msg", null, constraintName));
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(ex);

        // When & Then
        assertThatThrownBy(() -> userManagementService.createUser(request))
                .isSameAs(ex);

        verify(userRepository).existsByUsername(request.username());
        verify(userMapper).createUserFromRequest(request, passwordEncoder);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    /* =======================
       GET USER
       ======================= */

    @Order(5)
    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        UserResponseDto expectedResponse = createUserResponse("testUserDisplayName", "test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.getUser(userId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).findById(userId);
    }

    /* =======================
       UPDATE USER
       ======================= */

    @Order(6)
    @Test
    @DisplayName("Should update user by id successfully")
    void shouldUpdateUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        UpdateUserRequestDto updateRequest = createUpdateUserRequest("New Display Name");

        UserResponseDto expectedResponse = createUserResponse("New Display Name", "new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).updateUserFromRequest(updateRequest, testUser, passwordEncoder);
        verify(userRepository, never()).save(any());

    }

    /* =======================
       DELETE USER
       ======================= */

    @Order(7)
    @Test
    @DisplayName("Should delete user by id successfully")
    void shouldDeleteUserByIdSuccessfully() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        // When
        userManagementService.deleteUser(userId);
        // Then
        verify(userRepository).delete(testUser);
    }

    /* =======================
       EXCEPTIONS
       ======================= */

    @Order(8)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw EntityNotFoundException for non-existent user")
    void shouldThrowEntityNotFoundExceptionForNonExistentUser(TestUtils.Operation operation) {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, userId))
                .isInstanceOf(EntityNotFoundException.class);
        verify(userMapper, never()).updateUserFromRequest(any(), any(), any());
        verify(userRepository, never()).delete(any());
        verify(userMapper, never()).toResponse(any());
    }

    /* =======================
       HELPERS
       ======================= */

    private void executeOperation(TestUtils.Operation operation, Long userId) {
        switch (operation) {
            case GET -> userManagementService.getUser(userId);
            case UPDATE -> userManagementService.updateUser(userId, createUpdateUserRequest("New Name"));
            case DELETE -> userManagementService.deleteUser(userId);
        }
    }

    private CreateUserRequestDto createUserRequest() {
        return CreateUserRequestDto.builder()
                .username("testuser")
                .password("password123")
                .confirmPassword("password123")
                .email("test@email.com")
                .build();
    }

    private UpdateUserRequestDto createUpdateUserRequest(String displayName) {
        return UpdateUserRequestDto.builder()
                .password(null)
                .confirmPassword(null)
                .displayName(displayName)
                .email("new@email.com")
                .build();
    }

    private UserEntity createUserEntity(Long id) {
        return UserEntity.builder()
                .id(id)
                .displayName("testUserDisplayName")
                .username("testuser")
                .password("hashedPassword")
                .email("test@email.com")
                .build();

    }

    private UserResponseDto createUserResponse(String displayName, String email) {
        return UserResponseDto.builder()
                .id(1L)
                .displayName(displayName)
                .username("testuser")
                .email(email)
                .build();
    }
}