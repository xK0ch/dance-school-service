package de.tanzschule.service.event;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCleanupConfigRepository extends JpaRepository<EventCleanupConfig, UUID> {
}
