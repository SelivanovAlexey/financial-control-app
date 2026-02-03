package app.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "dto.user.response.description")
public record UserResponseDto(
        @Schema(description = "dto.user.id.description", example = "456")
        Long id,

        @Schema(description = "dto.user.username.description", example = "johndoe")
        String username,

        @Schema(description = "dto.user.displayName.description", example = "John Doe")
        String displayName,

        @Schema(description = "dto.user.email.description", example = "john.doe@example.com")
        String email) {
}
