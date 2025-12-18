package app.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

//TODO: разделить когда появлятся отличия в income и expense entities
public record UpdateTransactionBaseRequestDto(
        @Positive Long amount,
        String category,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @PastOrPresent OffsetDateTime createDate,
        @Size(max = 50) String description) {
}
