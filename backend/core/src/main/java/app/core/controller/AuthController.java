package app.core.controller;

import app.core.JsonHelper;
import app.core.model.dto.AuthRequestDto;
import app.core.model.dto.RegisterRequestDto;
import app.core.service.AuthServiceImpl;
import app.core.service.UserServiceImpl;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;
    private final JsonHelper jsonHelper;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> login(@Valid @RequestBody AuthRequestDto authRequest, HttpServletRequest request, HttpServletResponse response) {
        boolean isAuthSuccessful = authService.authenticate(authRequest.username(), authRequest.password(), authRequest.rememberMe(), request, response);
        return isAuthSuccessful ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(jsonHelper.createJson(Map.of("message","Неправильные имя пользователя или пароль")));
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> signUp(@Valid @RequestBody RegisterRequestDto registerRequest, HttpServletRequest request, HttpServletResponse response) {
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        userService.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        boolean isAuthSuccessful = authService.authenticate(registerRequest.username(), registerRequest.password(), false, request, response);
        return isAuthSuccessful ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(jsonHelper.createJson(Map.of("message","Неправильные имя пользователя или пароль")));
    }
}
