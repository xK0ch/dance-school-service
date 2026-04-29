package de.danceschool.service.event;

import de.danceschool.service.common.BaseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCleanupSchedulerTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTimeRangeRepository eventTimeRangeRepository;

    @Mock
    private EventCleanupConfigService configService;

    // Fixed at 2026-05-10; cutoff for cleanup is "today" (2026-05-10)
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-05-10T12:00:00Z"),
            ZoneId.of("UTC")
    );

    private EventCleanupScheduler scheduler;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        scheduler = new EventCleanupScheduler(eventRepository, eventTimeRangeRepository, configService, clock);
    }

    @Test
    void cleanup_whenDisabled_doesNothing() {
        when(configService.isEnabled()).thenReturn(false);

        scheduler.cleanupPastEvents();

        verify(eventRepository, never()).findAllByDateBefore(org.mockito.ArgumentMatchers.any());
        verify(eventRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void cleanup_whenEnabled_deletesPastEvents() {
        when(configService.isEnabled()).thenReturn(true);

        Event yesterday = stubEvent(LocalDate.of(2026, 5, 9));
        Event lastMonth = stubEvent(LocalDate.of(2026, 4, 30));
        when(eventRepository.findAllByDateBefore(eq(LocalDate.of(2026, 5, 10))))
                .thenReturn(List.of(yesterday, lastMonth));

        scheduler.cleanupPastEvents();

        verify(eventTimeRangeRepository, times(2)).deleteAllByEventId(org.mockito.ArgumentMatchers.any(UUID.class));
        verify(eventRepository).delete(yesterday);
        verify(eventRepository).delete(lastMonth);
    }

    @Test
    void cleanup_whenEnabledAndNothingToDelete_doesNotError() {
        when(configService.isEnabled()).thenReturn(true);
        when(eventRepository.findAllByDateBefore(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of());

        scheduler.cleanupPastEvents();

        verify(eventRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    private Event stubEvent(LocalDate date) {
        Event event = new Event("Event " + date, date, new BigDecimal("10"), null, null);
        try {
            java.lang.reflect.Field idField = BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(event, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return event;
    }
}
