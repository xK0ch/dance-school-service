package de.tanzschule.service.event;

import de.tanzschule.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record EventTimeRangeResponse(
        @NotNull UUID id,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull UUID eventId,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static EventTimeRangeResponse from(EventTimeRange timeRange) {
        return new EventTimeRangeResponse(
                timeRange.getId(),
                timeRange.getStartTime(),
                timeRange.getEndTime(),
                timeRange.getEvent().getId(),
                timeRange.getCreatedAt(),
                timeRange.getUpdatedAt()
        );
    }
}
