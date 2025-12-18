package app.core.model.dto;

public record UserResponseDto(
        Long id,
        String username,
        String displayName,
        String email
) {}
