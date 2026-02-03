package app.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

//TODO: разделить когда появятся отличия в income и expense entities
@Builder
@Schema(description = "dto.transaction.create.request.description")
public record CreateTransactionBaseRequestDto(
        @Schema(description = "dto.transaction.amount.description", example = "1500.00", minimum = "0.01")
        @NotNull @Positive BigDecimal amount,

        @Schema(description = "dto.transaction.category.description", example = "Продукты", maxLength = 128)
        @NotNull String category,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        @Schema(description = "dto.transaction.createDate.description", example = "2024-01-15T10:30:00+03:00")
        @NotNull @PastOrPresent OffsetDateTime createDate,

        @Schema(description = "dto.transaction.description.description", example = "Покупка продуктов", maxLength = 500)
        @Size(max = 50) String description) {
}