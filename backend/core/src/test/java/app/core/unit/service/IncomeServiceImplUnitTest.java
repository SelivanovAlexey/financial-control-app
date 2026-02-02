package app.core.unit.service;

import app.core.mappers.IncomeMapper;
import app.core.model.IncomeEntity;
import app.core.model.UserEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import app.core.repository.IncomeRepository;
import app.core.security.SecurityProvider;
import app.core.service.IncomeServiceImpl;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("IncomeServiceImpl Unit Tests")
@ActiveProfiles("unit")
class IncomeServiceImplUnitTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeMapper incomeMapper;

    @Mock
    private SecurityProvider securityProvider;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private final OffsetDateTime testDate =
            OffsetDateTime.parse("2024-01-01T10:00:00Z");

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
       CREATE
       ======================= */

    @Order(1)
    @Test
    @DisplayName("Should create income successfully when valid request provided")
    void shouldCreateIncomeSuccessfully() {
        // Given
        CreateTransactionBaseRequestDto request =
                createTransactionRequest(5000L, "Зарплата", "Месячная зарплата");

        IncomeEntity mappedEntity =
                createIncomeEntity(null, 5000L, "Зарплата", "Месячная зарплата", null);

        IncomeEntity savedEntity =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, 5000L, "Зарплата", "Месячная зарплата");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeMapper.createIncomeFromRequest(request)).thenReturn(mappedEntity);
        when(incomeRepository.save(any(IncomeEntity.class))).thenReturn(savedEntity);
        when(incomeMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = incomeService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(incomeMapper).createIncomeFromRequest(request);

        verify(incomeRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(testUser);
            assertThat(entity.getAmount()).isEqualTo(5000L);
            assertThat(entity.getCategory()).isEqualTo("Зарплата");
            assertThat(entity.getCreateDate()).isEqualTo(testDate);
            return true;
        }));
    }

    @Order(2)
    @Test
    @DisplayName("Should create income successfully when description is null")
    void shouldCreateIncomeSuccessfullyWhenDescriptionIsNull() {
        // Given
        CreateTransactionBaseRequestDto request =
                createTransactionRequest(5000L, "Зарплата", null);

        IncomeEntity mappedEntity =
                createIncomeEntity(null, 5000L, "Зарплата", null, null);

        IncomeEntity savedEntity =
                createIncomeEntity(1L, 5000L, "Зарплата", null, testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, 5000L, "Зарплата", null);

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeMapper.createIncomeFromRequest(request)).thenReturn(mappedEntity);
        when(incomeRepository.save(any(IncomeEntity.class))).thenReturn(savedEntity);
        when(incomeMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = incomeService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(incomeMapper).createIncomeFromRequest(request);

        verify(incomeRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(testUser);
            assertThat(entity.getAmount()).isEqualTo(5000L);
            assertThat(entity.getCategory()).isEqualTo("Зарплата");
            assertThat(entity.getCreateDate()).isEqualTo(testDate);
            assertThat(entity.getDescription()).isNull();
            return true;
        }));
    }

    /* =======================
       GET
       ======================= */

    @Order(3)
    @Test
    @DisplayName("Should get income successfully when income exists and user has access")
    void shouldGetIncomeSuccessfully() {
        // Given
        Long incomeId = 1L;
        IncomeEntity existingIncome =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, 5000L, "Зарплата", "Месячная зарплата");

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(existingIncome));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeMapper.toResponse(existingIncome)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = incomeService.get(incomeId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
    }

    @Order(4)
    @Test
    @DisplayName("Should return all user incomes successfully")
    void shouldReturnAllUserIncomesSuccessfully() {
        // Given
        List<IncomeEntity> userIncomes = List.of(
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser),
                createIncomeEntity(2L, 2000L, "Переводы", "Дополнительный доход", testUser)
        );

        List<TransactionBaseResponseDto> expectedResponses = List.of(
                createResponseDto(1L, 5000L, "Зарплата", "Месячная зарплата"),
                createResponseDto(2L, 2000L, "Переводы", "Дополнительный доход")
        );

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeRepository.findAllByUserId(testUser.getId())).thenReturn(userIncomes);
        when(incomeMapper.toResponse(userIncomes.get(0))).thenReturn(expectedResponses.get(0));
        when(incomeMapper.toResponse(userIncomes.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<TransactionBaseResponseDto> result = incomeService.getAllUserIncomes();

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(incomeRepository).findAllByUserId(testUser.getId());
    }

    @Order(5)
    @Test
    @DisplayName("Should return empty list when user has no incomes")
    void shouldReturnEmptyListWhenUserHasNoIncomes() {
        // Given
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeRepository.findAllByUserId(testUser.getId())).thenReturn(List.of());

        // When
        List<TransactionBaseResponseDto> result = incomeService.getAllUserIncomes();

        // Then
        assertThat(result).isEmpty();
        verify(incomeRepository).findAllByUserId(testUser.getId());
        verifyNoInteractions(incomeMapper);
    }

    /* =======================
       UPDATE
       ======================= */

    @Order(6)
    @Test
    @DisplayName("Should update income successfully when income exists and user has access")
    void shouldUpdateIncomeSuccessfully() {
        // Given
        Long incomeId = 1L;
        UpdateTransactionBaseRequestDto updateRequest =
                createUpdateRequest(6000L, "Переводы", "Годовая премия");

        IncomeEntity existingIncome =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser);

        IncomeEntity updatedIncome =
                createIncomeEntity(1L, 6000L, "Переводы", "Годовая премия", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, 6000L, "Переводы", "Годовая премия");

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(existingIncome));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeRepository.save(existingIncome)).thenReturn(updatedIncome);
        when(incomeMapper.toResponse(updatedIncome)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = incomeService.update(incomeId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(incomeMapper).updateIncomeFromRequest(updateRequest, existingIncome);
    }

    @Order(7)
    @Test
    @DisplayName("Should update income partially when some fields are null")
    void shouldUpdateIncomePartiallyWhenSomeFieldsAreNull() {
        // Given
        Long incomeId = 1L;
        UpdateTransactionBaseRequestDto partialUpdateRequest =
                createUpdateRequest(null, "Новая категория", null); // amount и description = null

        IncomeEntity existingIncome =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser);

        IncomeEntity expectedUpdatedIncome =
                createIncomeEntity(1L, 5000L, "Новая категория", "Месячная зарплата", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, 5000L, "Новая категория", "Месячная зарплата");

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(existingIncome));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(incomeRepository.save(existingIncome)).thenReturn(expectedUpdatedIncome);
        when(incomeMapper.toResponse(expectedUpdatedIncome)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = incomeService.update(incomeId, partialUpdateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(incomeMapper).updateIncomeFromRequest(partialUpdateRequest, existingIncome);
    }

    /* =======================
       DELETE
       ======================= */

    @Order(8)
    @Test
    @DisplayName("Should delete income successfully when income exists and user has access")
    void shouldDeleteIncomeSuccessfully() {
        // Given
        Long incomeId = 1L;
        IncomeEntity existingIncome =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", testUser);

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(existingIncome));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);

        // When
        incomeService.delete(incomeId);

        // Then
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(incomeRepository).delete(existingIncome);
    }

    /* =======================
       EXCEPTIONS
       ======================= */

    @Order(9)
    @Test
    @DisplayName("Should throw exception when security context returns null user on create")
    void shouldThrowExceptionWhenSecurityContextUserIsNullOnCreate() {
        // Given
        CreateTransactionBaseRequestDto request =
                createTransactionRequest(5000L, "Зарплата", "Месячная зарплата");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> incomeService.create(request))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(incomeRepository);
    }

    @Order(10)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw EntityNotFoundException for non-existent income")
    void shouldThrowEntityNotFoundExceptionForNonExistentIncome(TestUtils.Operation operation) {
        // Given
        Long incomeId = 1L;
        when(incomeRepository.findById(incomeId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, incomeId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Order(11)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw AccessDeniedException when accessing income of another user")
    void shouldThrowAccessDeniedExceptionWhenAccessingIncomeOfAnotherUser(TestUtils.Operation operation) {
        // Given
        Long incomeId = 1L;
        IncomeEntity incomeOfAnotherUser =
                createIncomeEntity(1L, 5000L, "Зарплата", "Месячная зарплата", otherUser);

        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(incomeOfAnotherUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        doThrow(AccessDeniedException.class)
                .when(securityProvider)
                .checkAccess(otherUser.getId(), testUser.getId());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, incomeId))
                .isInstanceOf(AccessDeniedException.class);
    }

    /* =======================
       HELPERS
       ======================= */

    private void executeOperation(TestUtils.Operation operation, Long incomeId) {
        switch (operation) {
            case GET -> incomeService.get(incomeId);
            case UPDATE -> incomeService.update(incomeId, createUpdateRequest(6000L, "Переводы", "Годовая премия"));
            case DELETE -> incomeService.delete(incomeId);
        }
    }

    private CreateTransactionBaseRequestDto createTransactionRequest(Long amount, String category, String description) {
        return CreateTransactionBaseRequestDto.builder()
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .build();
    }

    private UpdateTransactionBaseRequestDto createUpdateRequest(Long amount, String category, String description) {
        return UpdateTransactionBaseRequestDto.builder()
                .amount(amount)
                .category(category)
                .description(description)
                .build();
    }

    private IncomeEntity createIncomeEntity(Long id, Long amount, String category, String description, UserEntity user) {
        return IncomeEntity.builder()
                .id(id)
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .user(user)
                .build();
    }

    private TransactionBaseResponseDto createResponseDto(Long id, Long amount, String category, String description) {
        return TransactionBaseResponseDto.builder()
                .id(id)
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .build();
    }
}