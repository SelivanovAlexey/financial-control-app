package app.core.unit.mappers;

import app.core.mappers.IncomeMapper;
import app.core.model.IncomeEntity;
import app.core.model.dto.CreateTransactionBaseRequestDto;
import app.core.model.dto.TransactionBaseResponseDto;
import app.core.model.dto.UpdateTransactionBaseRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("IncomeMapper Unit Tests")
class IncomeMapperUnitTest {

    private IncomeMapper incomeMapper;

    private final OffsetDateTime currentDate = OffsetDateTime.now();
    private final OffsetDateTime oldDate = OffsetDateTime.now().minusDays(1);

    @BeforeEach
    void setUp() {
        incomeMapper = Mappers.getMapper(IncomeMapper.class);
    }

    @Test
    @DisplayName("Should create income from request")
    void shouldCreateIncomeFromRequest() {
        // Given
        CreateTransactionBaseRequestDto request = new CreateTransactionBaseRequestDto(
                50000L, "Salary", currentDate, "Monthly payment"
        );

        // When
        IncomeEntity result = incomeMapper.createIncomeFromRequest(request);

        // Then
        assertThat(result.getAmount()).isEqualTo(50000L);
        assertThat(result.getCategory()).isEqualTo("Salary");
        assertThat(result.getDescription()).isEqualTo("Monthly payment");
        assertThat(result.getCreateDate()).isEqualTo(currentDate);
        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
    }

    @Test
    @DisplayName("Should create income with null description")
    void shouldCreateIncomeWithNullDescription() {
        // Given
        CreateTransactionBaseRequestDto request = new CreateTransactionBaseRequestDto(
                10000L, "Freelance", currentDate, null
        );

        // When
        IncomeEntity result = incomeMapper.createIncomeFromRequest(request);

        // Then
        assertThat(result.getAmount()).isEqualTo(10000L);
        assertThat(result.getCategory()).isEqualTo("Freelance");
        assertThat(result.getDescription()).isNull();
        assertThat(result.getCreateDate()).isEqualTo(currentDate);
        assertThat(result.getId()).isNull();
        assertThat(result.getUser()).isNull();
    }

    @Test
    @DisplayName("Should update income from request with all fields")
    void shouldUpdateIncomeFromRequestWithAllFields() {
        // Given
        IncomeEntity income = IncomeEntity.builder()
                .id(1L)
                .amount(30000L)
                .category("Old Income")
                .description("Old Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                50000L, "New Income", currentDate, "New Description"
        );

        // When
        incomeMapper.updateIncomeFromRequest(request, income);

        // Then
        assertThat(income.getId()).isEqualTo(1L);
        assertThat(income.getAmount()).isEqualTo(50000L);
        assertThat(income.getCategory()).isEqualTo("New Income");
        assertThat(income.getDescription()).isEqualTo("New Description");
        assertThat(income.getCreateDate()).isEqualTo(currentDate);
        assertThat(income.getUser()).isNull();
    }

    @Test
    @DisplayName("Should partially update income (ignore null values)")
    void shouldPartiallyUpdateIncomeIgnoreNullValues() {
        // Given
        IncomeEntity income = IncomeEntity.builder()
                .amount(30000L)
                .category("Original Income")
                .description("Original Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                null, "Updated Income", null, null
        );

        // When
        incomeMapper.updateIncomeFromRequest(request, income);

        // Then
        assertThat(income.getAmount()).isEqualTo(30000L);
        assertThat(income.getCategory()).isEqualTo("Updated Income");
        assertThat(income.getDescription()).isEqualTo("Original Description");
        assertThat(income.getCreateDate()).isEqualTo(oldDate);
    }

    @Test
    @DisplayName("Should update income with zero/empty values")
    void shouldUpdateIncomeWithZeroEmptyValues() {
        // Given
        IncomeEntity income = IncomeEntity.builder()
                .amount(30000L)
                .category("Original Income")
                .description("Original Description")
                .createDate(oldDate)
                .build();

        UpdateTransactionBaseRequestDto request = new UpdateTransactionBaseRequestDto(
                0L, "", currentDate, ""
        );

        // When
        incomeMapper.updateIncomeFromRequest(request, income);

        // Then
        assertThat(income.getAmount()).isEqualTo(0L);
        assertThat(income.getCategory()).isEqualTo("");
        assertThat(income.getDescription()).isEqualTo("");
        assertThat(income.getCreateDate()).isEqualTo(currentDate);
    }

    @Test
    @DisplayName("Should convert income to response")
    void shouldConvertIncomeToResponse() {
        // Given
        IncomeEntity income = IncomeEntity.builder()
                .id(1L)
                .amount(50000L)
                .category("Salary")
                .description("Monthly payment")
                .createDate(currentDate)
                .build();

        // When
        TransactionBaseResponseDto result = incomeMapper.toResponse(income);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.amount()).isEqualTo(50000L);
        assertThat(result.category()).isEqualTo("Salary");
        assertThat(result.description()).isEqualTo("Monthly payment");
        assertThat(result.createDate()).isEqualTo(currentDate);
    }

    @Test
    @DisplayName("Should convert income with null description to response")
    void shouldConvertIncomeWithNullDescriptionToResponse() {
        // Given
        IncomeEntity income = IncomeEntity.builder()
                .id(2L)
                .amount(10000L)
                .category("Freelance")
                .description(null)
                .createDate(currentDate)
                .build();

        // When
        TransactionBaseResponseDto result = incomeMapper.toResponse(income);

        // Then
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.amount()).isEqualTo(10000L);
        assertThat(result.category()).isEqualTo("Freelance");
        assertThat(result.description()).isNull();
        assertThat(result.createDate()).isEqualTo(currentDate);
    }
}