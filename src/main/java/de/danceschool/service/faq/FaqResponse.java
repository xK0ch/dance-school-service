package de.danceschool.service.faq;

import de.danceschool.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record FaqResponse(
        @NotNull UUID id,
        @NotNull String question,
        @NotNull String answer,
        @NotNull Integer displayOrder,
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
