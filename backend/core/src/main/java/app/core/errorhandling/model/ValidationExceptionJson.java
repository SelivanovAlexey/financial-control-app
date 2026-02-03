package app.core.errorhandling.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

@Builder
@Schema(description = "error.exception.validation.description")
public record ValidationExceptionJson(
        @Schema(description = "error.exception.message.description", example = "Validation failed")
        String msg,

        @Schema(description = "error.exception.validation.errors.description", example = "{\"username\": \"Username is required\"}")
        Map<String, String> errors
) {}