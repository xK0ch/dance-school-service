package de.tanzschule.service.faq;

import de.tanzschule.service.common.BaseResponse;
import java.time.LocalDateTime;
import java.util.UUID;

public record FaqResponse(
        UUID id,
        String question,
        String answer,
        int displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
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
