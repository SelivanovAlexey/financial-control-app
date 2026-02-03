package app.core.unit.mappers;

import app.core.mappers.ExpenseMapper;
import app.core.model.ExpenseEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BIG_DECIMAL;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseMapper Unit Tests")
@ActiveProfiles("unit")
class ExpenseMapperUnitTest {


    private ExpenseMapper expenseMapper;

    @BeforeEach
    public void init() {
        expenseMapper = Mappers.getMapper(ExpenseMapper.class);
    }

    private final OffsetDateTime currentDate = OffsetDateTime.now();
    private final OffsetDateTime oldDate = OffsetDateTime.now().minusDays(1);

    @Test
    @DisplayName("Should create expense from request")
    void shouldCreateExpenseFromRequest() {
        // Given
        CreateTransactionBaseRequestDto request = new CreateTransactionBaseRequestDto(
                BigDecimal.valueOf(10000.21), "Food", currentDate, "Lunch"
        );

        // When
        ExpenseEntity result = expenseMapper.createExpenseFromRequest(request);

        // Then
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(10000.21));
        assertThat(result.getCategory()).isEqualTo("Food");
        assertThat(result.getDescription()).isEqualTo("Lunch");
        assertThat(result.getCreateDate()).isEqualTo(currentDate);
        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
    }

    @Test
    @DisplayName("Should create expense with null description")
    void shouldCreateExpenseWithNullDescription() {
        // Given
        CreateTransactionBaseRequestDto request = new CreateTransactionBaseRequestDto(
                BigDecimal.valueOf(5000.21), "Transport", currentDate, null
        );

        // When
        ExpenseEntity result = expenseMapper.createExpenseFromRequest(request);

        // Then
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(5000.21));
        assertThat(result.getCategory()).isEqualTo("Transport");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getCreateDate()).isEqualTo(currentDate);
        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
    }

    @Test
    @DisplayName("Should update expense from request with all fields")
    void shouldUpdateExpenseFromRequestWithAllFields() {
        // Given
        ExpenseEntity expense = ExpenseEntity.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50000.21))
                .category("Old Category")
                .description("Old Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                BigDecimal.valueOf(10000.21), "New Category", currentDate, "New Description"
        );

        // When
        expenseMapper.updateExpenseFromRequest(request, expense);

        // Then
        assertThat(expense.getId()).isEqualTo(1L);
        assertThat(expense.getAmount()).isEqualTo(BigDecimal.valueOf(10000.21));
        assertThat(expense.getCategory()).isEqualTo("New Category");
        assertThat(expense.getDescription()).isEqualTo("New Description");
        assertThat(expense.getCreateDate()).isEqualTo(currentDate);
        assertThat(expense.getUser()).isNull();
    }

    @Test
    @DisplayName("Should partially update expense (ignore null values)")
    void shouldPartiallyUpdateExpenseIgnoreNullValues() {
        // Given
        ExpenseEntity expense = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(5000.21))
                .category("Original Category")
                .description("Original Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                null, "Updated Category", null, null
        );

        // When
        expenseMapper.updateExpenseFromRequest(request, expense);

        // Then
        assertThat(expense.getAmount()).isEqualTo(BigDecimal.valueOf(5000.21));
        assertThat(expense.getCategory()).isEqualTo("Updated Category");
        assertThat(expense.getDescription()).isEqualTo("Original Description");
        assertThat(expense.getCreateDate()).isEqualTo(oldDate);
    }

    @Test
    @DisplayName("Should update expense with zero/empty values")
    void shouldUpdateExpenseWithZeroEmptyValues() {
        // Given
        ExpenseEntity expense = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(5000.21))
                .category("Original Category")
                .description("Original Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                BigDecimal.ZERO, "", currentDate, ""
        );

        // When
        expenseMapper.updateExpenseFromRequest(request, expense);

        // Then
        assertThat(expense.getAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(expense.getCategory()).isEqualTo("");
        assertThat(expense.getDescription()).isEqualTo("");
        assertThat(expense.getCreateDate()).isEqualTo(currentDate);
    }

    @Test
    @DisplayName("Should convert expense to response")
    void shouldConvertExpenseToResponse() {
        // Given
        ExpenseEntity expense = ExpenseEntity.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(10000.21))
                .category("Food")
                .description("Lunch")
                .createDate(currentDate)
                .build();

        // When
        TransactionBaseResponseDto result = expenseMapper.toResponse(expense);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(10000.21));
        assertThat(result.category()).isEqualTo("Food");
        assertThat(result.description()).isEqualTo("Lunch");
        assertThat(result.createDate()).isEqualTo(currentDate); // Теперь проверяем дату!
    }

    @Test
    @DisplayName("Should convert expense with null description to response")
    void shouldConvertExpenseWithNullDescriptionToResponse() {
        // Given
        ExpenseEntity expense = ExpenseEntity.builder()
                .id(2L)
                .amount(BigDecimal.valueOf(5000.21))
                .category("Transport")
                .description(null)
                .createDate(currentDate)
                .build();

        // When
        TransactionBaseResponseDto result = expenseMapper.toResponse(expense);

        // Then
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(5000.21));
        assertThat(result.category()).isEqualTo("Transport");
        assertThat(result.description()).isNull();
        assertThat(result.createDate()).isEqualTo(currentDate);
    }
}