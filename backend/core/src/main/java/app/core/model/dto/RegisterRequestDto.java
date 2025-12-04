package app.core.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDto(@NotNull String username,
                                 @NotNull String password,
                                 @NotNull String confirmPassword,
                                 @Email String email) {
}
