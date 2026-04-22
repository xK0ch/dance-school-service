package de.tanzschule.service.event;

import de.tanzschule.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventResponse(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull LocalDate date,
        BigDecimal entryCost,
        BigDecimal entryCostWithCustomerCard,
        String remark,
        @NotNull Integer displayOrder,
        @NotNull List<EventTimeRangeResponse> timeRanges,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getEntryCost(),
                event.getEntryCostWithCustomerCard(),
                event.getRemark(),
                event.getDisplayOrder(),
                event.getTimeRanges().stream()
                        .map(EventTimeRangeResponse::from)
                        .toList(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
