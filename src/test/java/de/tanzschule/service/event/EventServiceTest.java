package de.tanzschule.service.event;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTimeRangeRepository eventTimeRangeRepository;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sampleEvent = new Event(
                "Tanzball",
                LocalDate.of(2026, 12, 31),
                new BigDecimal("15.00"),
                new BigDecimal("10.00"),
                "Silvesterball"
        );
    }

    @Test
    void findAll_returnsAllEvents() {
        when(eventRepository.findAllByOrderByDateAsc()).thenReturn(List.of(sampleEvent));

        List<EventResponse> result = eventService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Tanzball");
    }

    @Test
    void findById_existingId_returnsEvent() {
        when(eventRepository.findWithTimeRangesById(id)).thenReturn(Optional.of(sampleEvent));

        EventResponse result = eventService.findById(id);

        assertThat(result.name()).isEqualTo("Tanzball");
        assertThat(result.entryCost()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(eventRepository.findWithTimeRangesById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.findById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
    }

    @Test
    void create_validRequest_savesEventWithTimeRanges() {
        EventRequest request = new EventRequest(
                "Sommerfest",
                LocalDate.of(2026, 7, 15),
                new BigDecimal("12.00"),
                new BigDecimal("8.00"),
                "Open Air",
                List.of(
                        new EventTimeRangeRequest(LocalTime.of(18, 0), LocalTime.of(20, 0)),
                        new EventTimeRangeRequest(LocalTime.of(21, 0), LocalTime.of(23, 0))
                )
        );

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.findWithTimeRangesById(any())).thenReturn(Optional.of(sampleEvent));
        when(eventTimeRangeRepository.save(any(EventTimeRange.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponse result = eventService.create(request);

        assertThat(result).isNotNull();
        verify(eventRepository).save(any(Event.class));
        // Two time ranges must be persisted
        verify(eventTimeRangeRepository, org.mockito.Mockito.times(2)).save(any(EventTimeRange.class));
    }

    @Test
    void create_minimalRequest_savesEventWithoutTimeRanges() {
        EventRequest request = new EventRequest(
                "Minimal Event",
                LocalDate.of(2026, 8, 1),
                null,
                null,
                null,
                null
        );

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(eventRepository.findWithTimeRangesById(any())).thenReturn(Optional.of(sampleEvent));

        EventResponse result = eventService.create(request);

        assertThat(result).isNotNull();
        verify(eventRepository).save(any(Event.class));
        verify(eventTimeRangeRepository, org.mockito.Mockito.never()).save(any(EventTimeRange.class));
    }

    @Test
    void update_existingId_updatesEventAndRecreatesTimeRanges() {
        when(eventRepository.findWithTimeRangesById(any())).thenReturn(Optional.of(sampleEvent));

        EventRequest request = new EventRequest(
                "Updated Event",
                LocalDate.of(2026, 9, 1),
                new BigDecimal("20.00"),
                null,
                "Updated remark",
                List.of(new EventTimeRangeRequest(LocalTime.of(19, 0), LocalTime.of(22, 0)))
        );

        EventResponse result = eventService.update(id, request);

        assertThat(result).isNotNull();
        verify(eventTimeRangeRepository).deleteAllByEventId(id);
        verify(eventTimeRangeRepository).save(any(EventTimeRange.class));
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(eventRepository.findWithTimeRangesById(nonExistingId)).thenReturn(Optional.empty());

        EventRequest request = new EventRequest(
                "Updated", LocalDate.of(2026, 10, 1), null, null, null, List.of()
        );

        assertThatThrownBy(() -> eventService.update(nonExistingId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesEventAndTimeRanges() {
        when(eventRepository.findWithTimeRangesById(id)).thenReturn(Optional.of(sampleEvent));

        eventService.delete(id);

        verify(eventTimeRangeRepository).deleteAllByEventId(id);
        verify(eventRepository).delete(sampleEvent);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(eventRepository.findWithTimeRangesById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
