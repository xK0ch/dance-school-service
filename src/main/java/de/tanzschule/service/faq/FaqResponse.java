package de.tanzschule.service.faq;

import de.tanzschule.service.common.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record FaqResponse(
        @NotNull UUID id,
        @NotNull String question,
        @NotNull String answer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int displayOrder,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static FaqResponse from(Faq faq) {
        return new FaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getDisplayOrder(),
                faq.getCreatedAt(),
                faq.getUpdatedAt()
        );
    }
}
