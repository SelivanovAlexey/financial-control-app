package app.core.controller;

import app.core.model.dto.AuthRequestDto;
import app.core.model.dto.CreateUserRequestDto;
import app.core.service.AuthServiceImpl;
import app.core.service.UserManagementServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    private final UserManagementServiceImpl userManagementService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> login(@Valid @RequestBody AuthRequestDto authRequest, HttpServletRequest request, HttpServletResponse response) {
        authService.authenticate(authRequest, request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> signUp(@Valid @RequestBody CreateUserRequestDto registerRequest, HttpServletRequest request, HttpServletResponse response) {
        userManagementService.createUser(registerRequest);
        AuthRequestDto authRequest = new AuthRequestDto(registerRequest.username(), registerRequest.password(), false);
        authService.authenticate(authRequest, request, response);
        return ResponseEntity.ok().build();
    }
}
