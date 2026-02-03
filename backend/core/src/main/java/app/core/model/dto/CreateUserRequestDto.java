package app.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "dto.user.create.request.description")
public record CreateUserRequestDto(
        @Schema(description = "dto.user.username.description", example = "johndoe")
        @NotNull
        String username,

        @Schema(description = "dto.user.password.description", example = "password123", minLength = 4)
        @NotNull @Size(min = 4)
        String password,

        @Schema(description = "dto.user.confirmPassword.description", example = "password123")
        @NotNull
        String confirmPassword,

        @Schema(description = "dto.user.displayName.description", example = "John Doe", maxLength = 128)
        @Size(max = 128)
        String displayName,

        @Schema(description = "dto.user.email.description", example = "john.doe@example.com")
        @Email
        String email) {
    @AssertTrue(message = "Passwords don't match")
    public boolean isPasswordMatch() {
        if (password != null) {
            return password.equals(confirmPassword);
        }
        return true;
    }
}
