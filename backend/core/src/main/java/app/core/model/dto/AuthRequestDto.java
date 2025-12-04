package app.core.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;;

public record AuthRequestDto(@NotNull String username,
                             @NotNull String password,
                             @Email String email,
                             boolean rememberMe) {
}
