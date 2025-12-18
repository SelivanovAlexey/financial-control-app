package app.core.repository;

import app.core.model.ExpenseEntity;
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
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    @Query("SELECT e FROM ExpenseEntity e WHERE e.user.id = :userId ORDER BY e.createDate DESC")
    List<ExpenseEntity> findAllByUserId(@Param("userId") Long userId);
}