package de.tanzschule.service.event;

import de.tanzschule.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventCleanupConfigResponse(
        @NotNull UUID id,
        @NotNull Boolean enabled,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static EventCleanupConfigResponse from(EventCleanupConfig config) {
        return new EventCleanupConfigResponse(
                config.getId(),
                config.isEnabled(),
                config.getCreatedAt(),
                config.getUpdatedAt()
        );
    }
}
