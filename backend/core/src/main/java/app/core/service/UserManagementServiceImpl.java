package app.core.service;

import app.core.utils.SecurityUtils;
import app.core.api.UserManagementService;
import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.mappers.UserMapper;
import app.core.model.UserEntity;
import app.core.model.dto.CreateUserRequestDto;
import app.core.model.dto.UpdateUserRequestDto;
import app.core.model.dto.UserResponseDto;
import app.core.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl extends CommonService implements UserManagementService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto newUser) throws UserAlreadyExistsException {
        Optional<UserEntity> user = userRepository.findByUsername(newUser.username());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User with username '" + newUser.username() + "' already exists");
        }

        UserEntity userEntity = userMapper.createUserFromRequest(newUser, passwordEncoder);
        userRepository.save(userEntity);

        log.debug("User {} successfully created", newUser.username());
        return userMapper.toResponse(userEntity);
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {
        deleteUser(getUserFromSecurityContext().getId());
    }

    @Override
    @Transactional
    public UserResponseDto updateCurrentUser(UpdateUserRequestDto userToUpdate) throws UsernameNotFoundException {
        return updateUser(getUserFromSecurityContext().getId(), userToUpdate);
    }

    @Override
    @Transactional
    public UserResponseDto getCurrentUser() {
        return getUser(getUserFromSecurityContext().getId());
    }

    @Override
    @Transactional
    public UserResponseDto getUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        SecurityUtils.checkAccess(userId, getUserFromSecurityContext().getId());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto userToUpdate) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        SecurityUtils.checkAccess(userId, getUserFromSecurityContext().getId());

        userMapper.updateUserFromRequest(userToUpdate, userEntity, passwordEncoder);

        if (userToUpdate.password() != null) {
            updateAuthenticationInSecurityContext(userEntity);
            log.debug("Password changed for user {}", userEntity.getUsername());
        }

        log.debug("User {} successfully updated", userEntity.getId());
        return userMapper.toResponse(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        SecurityUtils.checkAccess(userId, getUserFromSecurityContext().getId());

        userRepository.deleteById(userId);
        SecurityContextHolder.clearContext();

        log.debug("User {} successfully deleted", userId);
    }
}

