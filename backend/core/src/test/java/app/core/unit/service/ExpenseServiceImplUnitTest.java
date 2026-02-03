package app.core.unit.service;

import app.core.mappers.ExpenseMapper;
import app.core.model.ExpenseEntity;
import app.core.model.UserEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import app.core.repository.ExpenseRepository;
import app.core.security.SecurityProvider;
import app.core.service.ExpenseServiceImpl;
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

import java.math.BigDecimal;
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
@DisplayName("ExpenseServiceImpl Unit Tests")
@ActiveProfiles("unit")
class ExpenseServiceImplUnitTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private SecurityProvider securityProvider;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

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
    @DisplayName("Should create expense successfully when valid request provided")
    void shouldCreateExpenseSuccessfully() {
        // Given
        CreateTransactionBaseRequestDto request =
                createTransactionRequest(BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю");

        ExpenseEntity mappedEntity =
                createExpenseEntity(null, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", null);

        ExpenseEntity savedEntity =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseMapper.createExpenseFromRequest(request)).thenReturn(mappedEntity);
        when(expenseRepository.save(any(ExpenseEntity.class))).thenReturn(savedEntity);
        when(expenseMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = expenseService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(expenseMapper).createExpenseFromRequest(request);

        verify(expenseRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(testUser);
            assertThat(entity.getAmount()).isEqualTo(BigDecimal.valueOf(1500.21));
            assertThat(entity.getCategory()).isEqualTo("Продукты");
            assertThat(entity.getCreateDate()).isEqualTo(testDate);
            return true;
        }));
    }

    @Order(2)
    @Test
    @DisplayName("Should create expense successfully when description is null")
    void shouldCreateExpenseSuccessfullyWhenDescriptionIsNull() {
        // Given
        CreateTransactionBaseRequestDto request =
                createTransactionRequest(BigDecimal.valueOf(1500.21), "Продукты", null);

        ExpenseEntity mappedEntity =
                createExpenseEntity(null, BigDecimal.valueOf(1500.21), "Продукты", null, null);

        ExpenseEntity savedEntity =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", null, testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, BigDecimal.valueOf(1500.21), "Продукты", null);

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseMapper.createExpenseFromRequest(request)).thenReturn(mappedEntity);
        when(expenseRepository.save(any(ExpenseEntity.class))).thenReturn(savedEntity);
        when(expenseMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = expenseService.create(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);

        verify(expenseMapper).createExpenseFromRequest(request);

        verify(expenseRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(testUser);
            assertThat(entity.getAmount()).isEqualTo(BigDecimal.valueOf(1500.21));
            assertThat(entity.getCategory()).isEqualTo("Продукты");
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
    @DisplayName("Should get expense successfully when expense exists and user has access")
    void shouldGetExpenseSuccessfully() {
        // Given
        Long expenseId = 1L;
        ExpenseEntity existingExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseMapper.toResponse(existingExpense)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = expenseService.get(expenseId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
    }

    @Order(4)
    @Test
    @DisplayName("Should return all user expenses successfully")
    void shouldReturnAllUserExpensesSuccessfully() {
        // Given
        List<ExpenseEntity> userExpenses = List.of(
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser),
                createExpenseEntity(2L, BigDecimal.valueOf(2500.21), "Развлечения", "Поход в кино", testUser)
        );

        List<TransactionBaseResponseDto> expectedResponses = List.of(
                createResponseDto(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю"),
                createResponseDto(2L, BigDecimal.valueOf(2500.21), "Развлечения", "Поход в кино")
        );

        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseRepository.findAllByUserId(testUser.getId())).thenReturn(userExpenses);
        when(expenseMapper.toResponse(userExpenses.get(0))).thenReturn(expectedResponses.get(0));
        when(expenseMapper.toResponse(userExpenses.get(1))).thenReturn(expectedResponses.get(1));

        // When
        List<TransactionBaseResponseDto> result = expenseService.getAllUserExpenses();

        // Then
        assertThat(result).isEqualTo(expectedResponses);
        verify(expenseRepository).findAllByUserId(testUser.getId());
    }

    @Order(5)
    @Test
    @DisplayName("Should return empty list when user has no expenses")
    void shouldReturnEmptyListWhenUserHasNoExpenses() {
        // Given
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseRepository.findAllByUserId(testUser.getId())).thenReturn(List.of());

        // When
        List<TransactionBaseResponseDto> result = expenseService.getAllUserExpenses();

        // Then
        assertThat(result).isEmpty();
        verify(expenseRepository).findAllByUserId(testUser.getId());
        verifyNoInteractions(expenseMapper);
    }

    /* =======================
       UPDATE
       ======================= */

    @Order(6)
    @Test
    @DisplayName("Should update expense successfully when expense exists and user has access")
    void shouldUpdateExpenseSuccessfully() {
        // Given
        Long expenseId = 1L;
        UpdateTransactionBaseRequestDto updateRequest =
                createUpdateRequest(BigDecimal.valueOf(3000.21), "Медицина", "Визит к врачу");

        ExpenseEntity existingExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser);

        ExpenseEntity updatedExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(3000.21), "Медицина", "Визит к врачу", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, BigDecimal.valueOf(3000.21), "Медицина", "Визит к врачу");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseRepository.save(existingExpense)).thenReturn(updatedExpense);
        when(expenseMapper.toResponse(updatedExpense)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = expenseService.update(expenseId, updateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(expenseMapper).updateExpenseFromRequest(updateRequest, existingExpense);
    }

    @Order(7)
    @Test
    @DisplayName("Should update expense partially when some fields are null")
    void shouldUpdateExpensePartiallyWhenSomeFieldsAreNull() {
        // Given
        Long expenseId = 1L;
        UpdateTransactionBaseRequestDto partialUpdateRequest =
                createUpdateRequest(null, "Новая категория", null); // amount и description = null

        ExpenseEntity existingExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser);

        ExpenseEntity expectedUpdatedExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Новая категория", "Покупка продуктов на неделю", testUser);

        TransactionBaseResponseDto expectedResponse =
                createResponseDto(1L, BigDecimal.valueOf(1500.21), "Новая категория", "Покупка продуктов на неделю");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        when(expenseRepository.save(existingExpense)).thenReturn(expectedUpdatedExpense);
        when(expenseMapper.toResponse(expectedUpdatedExpense)).thenReturn(expectedResponse);

        // When
        TransactionBaseResponseDto result = expenseService.update(expenseId, partialUpdateRequest);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(expenseMapper).updateExpenseFromRequest(partialUpdateRequest, existingExpense);
    }

    /* =======================
       DELETE
       ======================= */

    @Order(8)
    @Test
    @DisplayName("Should delete expense successfully when expense exists and user has access")
    void shouldDeleteExpenseSuccessfully() {
        // Given
        Long expenseId = 1L;
        ExpenseEntity existingExpense =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", testUser);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);

        // When
        expenseService.delete(expenseId);

        // Then
        verify(securityProvider).checkAccess(testUser.getId(), testUser.getId());
        verify(expenseRepository).delete(existingExpense);
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
                createTransactionRequest(BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю");

        when(securityProvider.getUserFromSecurityContext()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> expenseService.create(request))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(expenseRepository);
    }

    @Order(10)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw EntityNotFoundException for non-existent expense")
    void shouldThrowEntityNotFoundExceptionForNonExistentExpense(TestUtils.Operation operation) {
        // Given
        Long expenseId = 1L;
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, expenseId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Order(11)
    @ParameterizedTest
    @EnumSource(value = TestUtils.Operation.class, names = {"GET", "UPDATE", "DELETE"})
    @DisplayName("Should throw AccessDeniedException when accessing expense of another user")
    void shouldThrowAccessDeniedExceptionWhenAccessingExpenseOfAnotherUser(TestUtils.Operation operation) {
        // Given
        Long expenseId = 1L;
        ExpenseEntity expenseOfAnotherUser =
                createExpenseEntity(1L, BigDecimal.valueOf(1500.21), "Продукты", "Покупка продуктов на неделю", otherUser);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expenseOfAnotherUser));
        when(securityProvider.getUserFromSecurityContext()).thenReturn(testUser);
        doThrow(AccessDeniedException.class)
                .when(securityProvider)
                .checkAccess(otherUser.getId(), testUser.getId());

        // When & Then
        assertThatThrownBy(() -> executeOperation(operation, expenseId))
                .isInstanceOf(AccessDeniedException.class);
    }

    /* =======================
       HELPERS
       ======================= */

    private void executeOperation(TestUtils.Operation operation, Long expenseId) {
        switch (operation) {
            case GET -> expenseService.get(expenseId);
            case UPDATE -> expenseService.update(expenseId, createUpdateRequest(BigDecimal.valueOf(3000.21), "Медицина", "Визит к врачу"));
            case DELETE -> expenseService.delete(expenseId);
        }
    }

    private CreateTransactionBaseRequestDto createTransactionRequest(BigDecimal amount, String category, String description) {
        return CreateTransactionBaseRequestDto.builder()
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .build();
    }

    private UpdateTransactionBaseRequestDto createUpdateRequest(BigDecimal amount, String category, String description) {
        return UpdateTransactionBaseRequestDto.builder()
                .amount(amount)
                .category(category)
                .description(description)
                .build();
    }

    private ExpenseEntity createExpenseEntity(Long id, BigDecimal amount, String category, String description, UserEntity user) {
        return ExpenseEntity.builder()
                .id(id)
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .user(user)
                .build();
    }

    private TransactionBaseResponseDto createResponseDto(Long id, BigDecimal amount, String category, String description) {
        return TransactionBaseResponseDto.builder()
                .id(id)
                .amount(amount)
                .category(category)
                .createDate(testDate)
                .description(description)
                .build();
    }
}