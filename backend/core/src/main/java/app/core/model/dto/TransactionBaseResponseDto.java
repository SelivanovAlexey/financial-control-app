package app.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.OffsetDateTime;

//TODO: разделить когда появлятся отличия в income и expense entities
@Builder
public record TransactionBaseResponseDto(
        Long id,
        Long amount,
        String category,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime createDate,
        String description) {
}
