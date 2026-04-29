package de.danceschool.service.event;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record EventTimeRangeRequest(
        @NotNull(message = "Start time must not be null")
        LocalTime startTime,

        @NotNull(message = "End time must not be null")
        LocalTime endTime
) {
}
