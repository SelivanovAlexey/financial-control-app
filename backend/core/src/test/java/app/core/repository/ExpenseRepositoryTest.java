package app.core.repository;

import app.core.model.ExpenseEntity;
import app.core.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ExpenseRepository Tests")
@ActiveProfiles("integration")
class ExpenseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExpenseRepository expenseRepository;

    private final OffsetDateTime currentDate = OffsetDateTime.now();

    @Test
    @DisplayName("Should find all expenses by user id when expenses exist")
    void shouldFindAllExpensesByUserIdWhenExpensesExist() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        ExpenseEntity expense1 = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(10000.21))
                .category("Food")
                .description("Lunch")
                .createDate(currentDate.minusDays(1))
                .user(user)
                .build();

        ExpenseEntity expense2 = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(5000.21))
                .category("Transport")
                .description("Bus ticket")
                .createDate(currentDate)
                .user(user)
                .build();

        entityManager.persist(expense1);
        entityManager.persistAndFlush(expense2);

        // When
        List<ExpenseEntity> result = expenseRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreateDate()).isAfterOrEqualTo(result.get(1).getCreateDate());
        assertThat(result).extracting(ExpenseEntity::getCategory)
                .containsExactlyInAnyOrder("Food", "Transport");
    }

    @Test
    @DisplayName("Should return empty list when user has no expenses")
    void shouldReturnEmptyListWhenUserHasNoExpenses() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // When
        List<ExpenseEntity> result = expenseRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should not return expenses from other users")
    void shouldNotReturnExpensesFromOtherUsers() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setDisplayName("User One");
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setDisplayName("User Two");
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        ExpenseEntity expense = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(10000.21))
                .category("Food")
                .description("Lunch")
                .createDate(currentDate)
                .user(user1)
                .build();
        entityManager.persistAndFlush(expense);

        // When
        List<ExpenseEntity> result = expenseRepository.findAllByUserId(user2.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return expenses ordered by create date descending")
    void shouldReturnExpensesOrderedByCreateDateDescending() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        OffsetDateTime now = currentDate;
        ExpenseEntity oldExpense = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(5000.21))
                .category("Old Expense")
                .createDate(now.minusDays(2))
                .user(user)
                .build();

        ExpenseEntity newExpense = ExpenseEntity.builder()
                .amount(BigDecimal.valueOf(10000.21))
                .category("New Expense")
                .createDate(now)
                .user(user)
                .build();

        entityManager.persist(oldExpense);
        entityManager.persistAndFlush(newExpense);

        // When
        List<ExpenseEntity> result = expenseRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategory()).isEqualTo("New Expense");
        assertThat(result.get(1).getCategory()).isEqualTo("Old Expense");
    }
}
