package app.core.controller;

import app.core.api.UserManagementService;
import app.core.errorhandling.exceptions.MethodNotSupportedException;
import app.core.model.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "api.users.tag", description = "api.users.tag.description")
public class UserController {
    private final UserManagementService userManagementService;

    @GetMapping("/me")
    @Operation(summary = "api.users.get.current")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.users.get.current.success"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<UserResponseDto> get() {
        return ResponseEntity.ok(userManagementService.getCurrentUser());
    }
    //TODO: to implement??
    @PostMapping
    @Operation(summary = "api.users.create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "api.users.create.not.supported")
    })
    public ResponseEntity<Void> create() {
        throw new MethodNotSupportedException("User creation is not supported yet from another user");
    }

    //TODO: to implement soft deletion
    @DeleteMapping("/me")
    @Operation(summary = "api.users.delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "405", description = "api.users.delete.not.supported")
    })
    public ResponseEntity<Void> delete() {
        throw new MethodNotSupportedException("User deletion is not supported yet");
    }

    @PatchMapping("/me")
    @Operation(summary = "api.users.update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.users.update.success"),
            @ApiResponse(responseCode = "400", description = "error.validation"),
            @ApiResponse(responseCode = "401", description = "error.unauthorized"),
            @ApiResponse(responseCode = "403", description = "error.forbidden"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<UserResponseDto> update(@Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        return ResponseEntity.ok(userManagementService.updateCurrentUser(updateUserRequest));

    }
}
