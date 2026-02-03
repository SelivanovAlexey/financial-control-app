package app.core.errorhandling.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "error.exception.common.description")
public record CommonExceptionJson(
        @Schema(description = "error.exception.message.description", example = "Something went wrong")
        String msg,

        @Schema(description = "error.exception.cause.description", example = "Detailed error cause")
        String cause
) {}
