package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CourseTariffResponse(
        UUID id,
        String name,
        BigDecimal price,
        UUID courseId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseResponse {

    public static CourseTariffResponse from(CourseTariff tariff) {
        return new CourseTariffResponse(
                tariff.getId(),
                tariff.getName(),
                tariff.getPrice(),
                tariff.getCourse().getId(),
                tariff.getCreatedAt(),
                tariff.getUpdatedAt()
        );
    }
}
