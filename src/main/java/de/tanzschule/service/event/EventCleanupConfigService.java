package de.tanzschule.service.event;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventCleanupConfigService {

    private final EventCleanupConfigRepository repository;

    @Transactional(readOnly = true)
    public EventCleanupConfigResponse getConfig() {
        return EventCleanupConfigResponse.from(load());
    }

    @Transactional
    public EventCleanupConfigResponse updateConfig(EventCleanupConfigRequest request) {
        EventCleanupConfig config = load();
        config.setEnabled(Boolean.TRUE.equals(request.enabled()));
        config.setUpdatedAt(LocalDateTime.now());
        return EventCleanupConfigResponse.from(repository.save(config));
    }

    @Transactional(readOnly = true)
    public boolean isEnabled() {
        return repository.findAll().stream()
                .findFirst()
                .map(EventCleanupConfig::isEnabled)
                .orElse(true);
    }

    private EventCleanupConfig load() {
        return repository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event cleanup config row not found — should have been seeded by Flyway migration V11"));
    }
}
