package app.core.repository;

import app.core.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Expense в базе данных.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также возможность добавления собственных запросов
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId ORDER BY e.createDate DESC")
    List<Expense> findAllByUserId(@Param("userId") Long userId);
}