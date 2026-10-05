package app.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "dto.auth.request.description")
public record AuthRequestDto(
        @Schema(description = "dto.auth.username.description", example = "georgeWashington")
        @NotBlank @Size(max = 128)
        String username,

        @Schema(description = "dto.auth.password.description", example = "password123", minLength = 4)
        @NotNull @Size(min = 4, max = 72)
        String password,

        @Schema(description = "dto.auth.rememberMe.description", example = "true")
        boolean rememberMe) {
}