package de.tanzschule.service.faq;

import java.time.LocalDateTime;

public record FaqResponse(
        Long id,
        String question,
        String answer,
        int displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

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
