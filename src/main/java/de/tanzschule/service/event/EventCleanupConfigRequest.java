package de.tanzschule.service.event;

import jakarta.validation.constraints.NotNull;

public record EventCleanupConfigRequest(
        @NotNull Boolean enabled
) {
}
