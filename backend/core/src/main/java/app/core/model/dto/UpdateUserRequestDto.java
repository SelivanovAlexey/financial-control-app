package app.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "dto.user.update.request.description")
public record UpdateUserRequestDto(
        @Schema(description = "dto.user.password.description", example = "newpassword123", minLength = 4)
        @Size(min = 4)
        String password,

        @Schema(description = "dto.user.confirmPassword.description", example = "newpassword123")
        String confirmPassword,

        @Schema(description = "dto.user.displayName.description", example = "Updated Name", maxLength = 128)
        @Size(max = 128)
        String displayName,

        @Schema(description = "dto.user.email.description", example = "newemail@example.com")
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
