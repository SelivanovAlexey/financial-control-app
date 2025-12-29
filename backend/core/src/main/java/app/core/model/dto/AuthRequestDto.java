package app.core.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthRequestDto(
        @NotNull
        String username,
        @NotNull @Size(min = 4)
        String password,
        boolean rememberMe) {
}
