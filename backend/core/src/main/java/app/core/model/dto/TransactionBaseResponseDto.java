package app.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

//TODO: разделить когда появятся отличия в income и expense entities
@Builder
@Schema(description = "dto.transaction.response.description")
public record TransactionBaseResponseDto(
        @Schema(description = "dto.transaction.id.description", example = "123")
        Long id,

        @Schema(description = "dto.transaction.amount.description", example = "1500.00")
        BigDecimal amount,

        @Schema(description = "dto.transaction.category.description", example = "Продукты")
        String category,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @Schema(description = "dto.transaction.createDate.description", example = "2024-01-15T10:30:00+03:00")
        OffsetDateTime createDate,

        @Schema(description = "dto.transaction.description.description", example = "Покупка продуктов на неделю")
        String description) {
}
