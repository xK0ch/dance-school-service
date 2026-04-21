package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CourseTariffResponse(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull BigDecimal price,
        @NotNull UUID courseId,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
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
