package de.tanzschule.service.event;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventCleanupScheduler {

    private final EventRepository eventRepository;
    private final EventTimeRangeRepository eventTimeRangeRepository;
    private final EventCleanupConfigService configService;
    private final Clock clock;

    /**
     * Runs daily at 03:00 server time. Deletes every event whose date is before
     * the first day of the current month, so last month's events disappear as
     * soon as the new month starts.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupPastEvents() {
        if (!configService.isEnabled()) {
            log.debug("Event cleanup is disabled; skipping run.");
            return;
        }

        LocalDate firstOfThisMonth = LocalDate.now(clock).withDayOfMonth(1);
        List<Event> toDelete = eventRepository.findAllByDateBefore(firstOfThisMonth);

        if (toDelete.isEmpty()) {
            log.info("Event cleanup: nothing to delete (cutoff: {}).", firstOfThisMonth);
            return;
        }

        for (Event event : toDelete) {
            UUID id = event.getId();
            eventTimeRangeRepository.deleteAllByEventId(id);
            eventRepository.delete(event);
        }

        log.info("Event cleanup: deleted {} event(s) before {}.", toDelete.size(), firstOfThisMonth);
    }
}
