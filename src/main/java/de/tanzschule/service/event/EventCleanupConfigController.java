package de.tanzschule.service.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-cleanup-config")
@Tag(name = "Event Cleanup Config", description = "Configuration for automatic event cleanup")
@RequiredArgsConstructor
public class EventCleanupConfigController {

    private final EventCleanupConfigService service;

    @GetMapping
    @Operation(operationId = "getEventCleanupConfig", summary = "Get event cleanup config", description = "Returns whether the automatic cleanup of past events is enabled (requires authentication)")
    public EventCleanupConfigResponse getConfig() {
        return service.getConfig();
    }

    @PutMapping
    @Operation(operationId = "updateEventCleanupConfig", summary = "Update event cleanup config", description = "Enable or disable automatic cleanup of past events (requires authentication)")
    public EventCleanupConfigResponse updateConfig(@Valid @RequestBody EventCleanupConfigRequest request) {
        return service.updateConfig(request);
    }
}
