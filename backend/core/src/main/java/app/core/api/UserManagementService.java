package app.core.api;

import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;

/**
 * Сервис управления юзерами (создание, удаление, обновление, получение)
 */
public interface UserManagementService {
    UserResponseDto createUser(CreateUserRequestDto newUser);

    UserResponseDto updateUser(Long id, UpdateUserRequestDto userToUpdate);

    UserResponseDto getUser(Long id);

    void deleteUser(Long id);
}
