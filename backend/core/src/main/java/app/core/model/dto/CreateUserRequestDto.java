package app.core.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequestDto(
        @NotNull
        String username,
        @NotNull @Size(min = 4)
        String password,
        @NotNull
        String confirmPassword,
        @Size(max = 128)

        String displayName,
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
