package app.core.repository;

import app.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Uert в базе данных.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также возможность добавления собственных запросов
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}