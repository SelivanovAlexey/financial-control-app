package app.core.unit.service;

import app.core.mappers.UserMapper;
import app.core.model.UserEntity;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import app.core.repository.UserRepository;
import app.core.security.SecurityProvider;
import app.core.service.UserManagementServiceImpl;
import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.unit.utils.TestUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
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

    @Mock
    private SecurityProvider securityProvider;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private final UserEntity testUser = new UserEntity(
            1L,
            "testUserDisplayName",
            "testuser",
            "hashedPassword",
            "test@email.com"
    );

    private final UserEntity otherUser = new UserEntity(
            2L,
            "otherUserDisplayName",
            "otheruser",
            "otherHashedPassword",
            "other@email.com"
    );

    /* =======================
       CREATE USER
       ======================= */

    @Order(1)
    @Test
    @DisplayName("Should create user successfully when valid request provided")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequestDto request = createUserRequest("testuser", "password123", "test@email.com");

        UserEntity mappedEntity = createUserEntity(null, "testUserDisplayName", "testuser", "hashedPassword", "test@email.com");
        UserEntity savedEntity = createUserEntity(1L, "testUserDisplayName", "testuser", "hashedPassword", "test@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "testUserDisplayName", "testuser", "test@email.com");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userMapper.createUserFromRequest(request, passwordEncoder)).thenReturn(mappedEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.createUser(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(userRepository).findByUsername(request.username());
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
        CreateUserRequestDto request = createUserRequest("testuser", "password123", "test@email.com");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userManagementService.createUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with username 'testuser' already exists");

        verify(userRepository).findByUsername(request.username());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    /* =======================
       GET USER
       ======================= */

    @Order(3)
    @Test
    @DisplayName("Should get current user successfully")
    void shouldGetCurrentUserSuccessfully() {
        // Given
        UserResponseDto expectedResponse = createUserResponse(1L, "testUserDisplayName", "testuser", "test@email.com");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.getCurrentUser();

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider, times(2)).getUserFromSecurityContext();
        verify(userRepository).findById(testUser.getId());
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(userMapper).toResponse(testUser);
    }

    @Order(4)
    @Test
    @DisplayName("Should get user by id successfully when user has access")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        UserResponseDto expectedResponse = createUserResponse(1L, "testUserDisplayName", "testuser", "test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.getUser(userId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
    }

    /* =======================
       UPDATE USER
       ======================= */

    @Order(5)
    @Test
    @DisplayName("Should update current user successfully")
    void shouldUpdateCurrentUserSuccessfully() {
        // Given
        UpdateUserRequestDto updateRequest = createUpdateUserRequest("newPassword123", "newPassword123", "New Display Name", "new@email.com");

        UserEntity updatedUser = createUserEntity(1L, "New Display Name", "testuser", "newHashedPassword", "new@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "New Display Name", "testuser", "new@email.com");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateCurrentUser(updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider, times(2)).getUserFromSecurityContext();
        verify(userRepository).findById(testUser.getId());
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(userMapper).updateUserFromRequest(updateRequest, testUser, passwordEncoder);
        verify(userRepository).save(testUser);
        verify(securityProvider).updateAuthenticationInSecurityContext(testUser);
    }

    @Order(6)
    @Test
    @DisplayName("Should update user by id successfully when user has access")
    void shouldUpdateUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        UpdateUserRequestDto updateRequest = createUpdateUserRequest(null, null, "New Display Name", "new@email.com");

        UserEntity updatedUser = createUserEntity(1L, "New Display Name", "testuser", "hashedPassword", "new@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "New Display Name", "testuser", "new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(userMapper).updateUserFromRequest(updateRequest, testUser, passwordEncoder);
        verify(userRepository).save(testUser);
    }

    /* =======================
       DELETE USER
       ======================= */

    @Order(7)
    @Test
    @DisplayName("Should delete current user successfully")
    void shouldDeleteCurrentUserSuccessfully() {
        // Given
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        userManagementService.deleteCurrentUser();

        // Then
        verify(securityProvider, times(2)).getUserFromSecurityContext();
        verify(userRepository).findById(testUser.getId());
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(userRepository).deleteById(testUser.getId());
    }

    @Order(8)
    @Test
    @DisplayName("Should delete user by id successfully when user has access")
    void shouldDeleteUserByIdSuccessfully() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);

        // When
        userManagementService.deleteUser(userId);

        // Then
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(userRepository).deleteById(userId);
    }

    /* =======================
       EXCEPTIONS
       ======================= */

    @Order(9)
    @Test
    @DisplayName("Should throw exception when security context returns null user on get current")
    void shouldThrowExceptionWhenSecurityContextUserIsNullOnGetCurrent() {
        // Given
        when(securityProvider.getUserFromSecurityContext()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userManagementService.getCurrentUser())
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(userRepository);
    }

    @Order(10)
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
    }

    @Order(11)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw AccessDeniedException when accessing another user")
    void shouldThrowAccessDeniedExceptionWhenAccessingAnotherUser(TestUtils.Operation operation) {
        // Given
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(otherUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        doThrow(AccessDeniedException.class)
                .when(securityProvider)
                .checkAccess(otherUser.getId(), testUser.getId());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    /* =======================
   PARTIAL UPDATE/CREATE TESTS
   ======================= */

    @Order(12)
    @Test
    @DisplayName("Should create user with generated displayName when displayName not provided")
    void shouldCreateUserWithGeneratedDisplayName() {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .username("testuser")
                .password("password123")
                .confirmPassword("password123")
                .email("test@email.com")
                // displayName = null, should be generated from username
                .build();

        UserEntity mappedEntity = createUserEntity(null, "testuser", "testuser", "hashedPassword", "test@email.com");
        UserEntity savedEntity = createUserEntity(1L, "testuser", "testuser", "hashedPassword", "test@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "testuser", "testuser", "test@email.com");

        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userMapper.createUserFromRequest(request, passwordEncoder)).thenReturn(mappedEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.createUser(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).createUserFromRequest(request, passwordEncoder);
    }

    @Order(13)
    @Test
    @DisplayName("Should update only email when other fields are null")
    void shouldUpdateOnlyEmail() {
        // Given
        Long userId = 1L;
        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .email("new@email.com")
                // password = null, displayName = null
                .build();

        UserEntity existingUser = createUserEntity(1L, "Old Display Name", "testuser", "oldPassword", "old@email.com");
        UserEntity updatedUser = createUserEntity(1L, "Old Display Name", "testuser", "oldPassword", "new@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "Old Display Name", "testuser", "new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).updateUserFromRequest(updateRequest, existingUser, passwordEncoder);
        verify(userRepository).save(existingUser);
        verify(securityProvider).getUserFromSecurityContext();
    }

    @Order(14)
    @Test
    @DisplayName("Should update only displayName when other fields are null")
    void shouldUpdateOnlyDisplayName() {
        // Given
        Long userId = 1L;
        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .displayName("New Display Name")
                // password = null, email = null
                .build();

        UserEntity existingUser = createUserEntity(1L, "Old Display Name", "testuser", "oldPassword", "old@email.com");
        UserEntity updatedUser = createUserEntity(1L, "New Display Name", "testuser", "oldPassword", "old@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "New Display Name", "testuser", "old@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).updateUserFromRequest(updateRequest, existingUser, passwordEncoder);
        verify(userRepository).save(existingUser);
        verify(securityProvider).getUserFromSecurityContext();
    }

    @Order(15)
    @Test
    @DisplayName("Should update only password and trigger auth context update")
    void shouldUpdateOnlyPasswordAndTriggerAuthUpdate() {
        // Given
        Long userId = 1L;
        UpdateUserRequestDto updateRequest = UpdateUserRequestDto.builder()
                .password("newPassword123")
                .confirmPassword("newPassword123")
                // displayName = null, email = null
                .build();

        UserEntity existingUser = createUserEntity(1L, "Display Name", "testuser", "oldPassword", "test@email.com");
        UserEntity updatedUser = createUserEntity(1L, "Display Name", "testuser", "newHashedPassword", "test@email.com");
        UserResponseDto expectedResponse = createUserResponse(1L, "Display Name", "testuser", "test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        // When
        UserResponseDto result = userManagementService.updateUser(userId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).updateUserFromRequest(updateRequest, existingUser, passwordEncoder);
        verify(userRepository).save(existingUser);
        verify(securityProvider).updateAuthenticationInSecurityContext(existingUser);
        verify(securityProvider).getUserFromSecurityContext();
    }

    @Order(16)
    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent user")
    void shouldThrowEntityNotFoundExceptionWhenDeletingNonExistentUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userManagementService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User with id: " + userId + " is not found!");
    }

    /* =======================
       HELPERS
       ======================= */

    private void executeOperation(TestUtils.Operation operation, Long userId) {
        switch (operation) {
            case GET -> userManagementService.getUser(userId);
            case UPDATE -> userManagementService.updateUser(userId, createUpdateUserRequest(null, null, "New Name", "new@email.com"));
            case DELETE -> userManagementService.deleteUser(userId);
        }
    }

    private CreateUserRequestDto createUserRequest(String username, String password, String email) {
        return CreateUserRequestDto.builder()
                .username(username)
                .password(password)
                .confirmPassword(password)
                .email(email)
                .build();
    }

    private UpdateUserRequestDto createUpdateUserRequest(String password, String confirmPassword, String displayName, String email) {
        return UpdateUserRequestDto.builder()
                .password(password)
                .confirmPassword(confirmPassword)
                .displayName(displayName)
                .email(email)
                .build();
    }

    private UserEntity createUserEntity(Long id, String displayName, String username, String password, String email) {
        return new UserEntity(id, displayName, username, password, email);
    }

    private UserResponseDto createUserResponse(Long id, String displayName, String username, String email) {
        return UserResponseDto.builder()
                .id(id)
                .displayName(displayName)
                .username(username)
                .email(email)
                .build();
    }
}