package app.core.controller;

import app.core.api.UserManagementService;
import app.core.errorhandling.exceptions.MethodNotSupportedException;
import app.core.model.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserManagementService userManagementService;

    @GetMapping("/me")
    public UserResponseDto get() {
        return userManagementService.getCurrentUser();
    }

    //TODO: to implement??
    @PostMapping
    public UserResponseDto create() {
        throw new MethodNotSupportedException("User creation is not supported yet from another user");
    }

    //TODO: to implement soft deletion
    @DeleteMapping("/me")
    public void delete() {
        throw new MethodNotSupportedException("User deletion is not supported yet");
    }

    @PatchMapping("/me")
    public UserResponseDto update(@Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        return userManagementService.updateCurrentUser(updateUserRequest);
    }
}
