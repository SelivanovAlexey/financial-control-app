package app.core.model.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String displayName,
        String email
) {}
