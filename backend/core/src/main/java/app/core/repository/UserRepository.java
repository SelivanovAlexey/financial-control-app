package app.core.repository;

import app.core.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User в базе данных.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также возможность добавления собственных запросов
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT i FROM UserEntity i WHERE i.username = :username")
    Optional<UserEntity> findByUsername(@Param("username") String username);
}