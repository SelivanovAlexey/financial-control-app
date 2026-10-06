package app.core.service;

import app.core.errorhandling.utils.ConstraintViolations;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//TODO: че как много transactional?
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponseDto createUser(CreateUserRequestDto newUser) {
        if (userRepository.existsByUsername(newUser.username())) {
            throw new UserAlreadyExistsException("User with username '" + newUser.username() + "' already exists");
        }
        UserEntity userEntity = userMapper.createUserFromRequest(newUser, passwordEncoder);
        UserEntity savedUser;
        try {
            savedUser = userRepository.save(userEntity);
        } catch (DataIntegrityViolationException exception) {
            switch (ConstraintViolations.getConstraint(exception)) {
                //TODO: inspect how to gather constraint name from ORM
                case "uq_users_username" ->
                        throw new UserAlreadyExistsException("User with username '" + newUser.username() + "' already exists");
                case null, default -> throw exception;
            }
        }
        log.debug("User {} successfully created", newUser.username());
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto userToUpdate) {
        UserEntity userEntity = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        userMapper.updateUserFromRequest(userToUpdate, userEntity, passwordEncoder);
        log.debug("User {} successfully updated", userEntity.getId());
        return userMapper.toResponse(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " is not found!"));
        userRepository.delete(user);
        log.debug("User {} successfully deleted", userId);
    }
}

