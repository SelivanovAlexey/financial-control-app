package app.core.repository;

import app.core.model.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Income в базе данных.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также возможность добавления собственных запросов
 */
@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    @Query("SELECT i FROM IncomeEntity i WHERE i.user.id = :userId ORDER BY i.createDate DESC")
    List<IncomeEntity> findAllByUserId(@Param("userId") Long userId);
}