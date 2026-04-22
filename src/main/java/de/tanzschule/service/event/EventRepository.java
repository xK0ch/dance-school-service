package de.tanzschule.service.event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @EntityGraph(attributePaths = "timeRanges")
    List<Event> findAllByOrderByDateAscDisplayOrderAsc();

    @EntityGraph(attributePaths = "timeRanges")
    Optional<Event> findWithTimeRangesById(UUID id);
}
