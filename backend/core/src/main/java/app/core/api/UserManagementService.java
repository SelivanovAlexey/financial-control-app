package app.core.api;

import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Сервис управления юзерами (создание, удаление, обновление, получение)
 */
public interface UserManagementService {
    UserResponseDto createUser(CreateUserRequestDto newUser) throws UserAlreadyExistsException;

    void deleteCurrentUser();

    UserResponseDto updateCurrentUser(UpdateUserRequestDto userToUpdate) throws UsernameNotFoundException;

    UserResponseDto getCurrentUser();

    void deleteUser(Long id);

    UserResponseDto updateUser(Long id, UpdateUserRequestDto userToUpdate) throws UsernameNotFoundException;

    UserResponseDto getUser(Long id);
}
