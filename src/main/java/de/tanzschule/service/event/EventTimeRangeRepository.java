package de.tanzschule.service.event;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTimeRangeRepository extends JpaRepository<EventTimeRange, UUID> {

    List<EventTimeRange> findAllByEventId(UUID eventId);

    void deleteAllByEventId(UUID eventId);
}
