package app.core.repository;

import app.core.model.IncomeEntity;
import app.core.model.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;


import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("IncomeRepository Tests")
@ActiveProfiles("integration")
class IncomeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IncomeRepository incomeRepository;

    private final OffsetDateTime currentDate = OffsetDateTime.now();

    @Test
    @DisplayName("Should find all incomes by user id when incomes exist")
    void shouldFindAllIncomesByUserIdWhenIncomesExist() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        IncomeEntity income1 = IncomeEntity.builder()
                .amount(50000L)
                .category("Salary")
                .description("Monthly salary")
                .createDate(currentDate.minusDays(1))
                .user(user)
                .build();

        IncomeEntity income2 = IncomeEntity.builder()
                .amount(10000L)
                .category("Freelance")
                .description("Project payment")
                .createDate(currentDate)
                .user(user)
                .build();

        entityManager.persist(income1);
        entityManager.persistAndFlush(income2);

        // When
        List<IncomeEntity> result = incomeRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreateDate()).isAfterOrEqualTo(result.get(1).getCreateDate()); // DESC order
        assertThat(result).extracting(IncomeEntity::getCategory)
                .containsExactlyInAnyOrder("Salary", "Freelance");
    }

    @Test
    @DisplayName("Should return empty list when user has no incomes")
    void shouldReturnEmptyListWhenUserHasNoIncomes() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        // When
        List<IncomeEntity> result = incomeRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should not return incomes from other users")
    void shouldNotReturnIncomesFromOtherUsers() {
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

        IncomeEntity income = IncomeEntity.builder()
                .amount(50000L)
                .category("Salary")
                .description("Monthly salary")
                .createDate(currentDate)
                .user(user1)
                .build();
        entityManager.persistAndFlush(income);

        // When
        List<IncomeEntity> result = incomeRepository.findAllByUserId(user2.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return incomes ordered by create date descending")
    void shouldReturnIncomesOrderedByCreateDateDescending() {
        // Given
        UserEntity user = new UserEntity();
        user.setDisplayName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        OffsetDateTime now = currentDate;
        IncomeEntity oldIncome = IncomeEntity.builder()
                .amount(30000L)
                .category("Old Income")
                .createDate(now.minusDays(2))
                .user(user)
                .build();

        IncomeEntity newIncome = IncomeEntity.builder()
                .amount(50000L)
                .category("New Income")
                .createDate(now)
                .user(user)
                .build();

        entityManager.persist(oldIncome);
        entityManager.persistAndFlush(newIncome);

        // When
        List<IncomeEntity> result = incomeRepository.findAllByUserId(user.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategory()).isEqualTo("New Income");
        assertThat(result.get(1).getCategory()).isEqualTo("Old Income");
    }
}