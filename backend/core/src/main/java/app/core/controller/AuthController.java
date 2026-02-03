package app.core.controller;

import app.core.model.dto.AuthRequestDto;
import app.core.model.dto.CreateUserRequestDto;
import app.core.service.AuthServiceImpl;
import app.core.service.UserManagementServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "api.auth.tag", description = "api.auth.tag.description")
public class AuthController {
    private final AuthServiceImpl authService;
    private final UserManagementServiceImpl userManagementService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "api.auth.login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "api.auth.login.success"),
            @ApiResponse(responseCode = "400", description = "error.validation"),
            @ApiResponse(responseCode = "401", description = "api.auth.login.failed"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<Void> login(@Valid @RequestBody AuthRequestDto authRequest, HttpServletRequest request, HttpServletResponse response) {
        authService.authenticate(authRequest, request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "api.auth.signup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "api.auth.signup.success"),
            @ApiResponse(responseCode = "400", description = "error.validation"),
            @ApiResponse(responseCode = "409", description = "api.auth.user.exists"),
            @ApiResponse(responseCode = "405", description = "error.method.not.allowed"),
            @ApiResponse(responseCode = "500", description = "error.internal.server")
    })
    public ResponseEntity<Void> signUp(@Valid @RequestBody CreateUserRequestDto registerRequest, HttpServletRequest request, HttpServletResponse response) {
        userManagementService.createUser(registerRequest);
        AuthRequestDto authRequest = new AuthRequestDto(registerRequest.username(), registerRequest.password(), false);
        authService.authenticate(authRequest, request, response);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
