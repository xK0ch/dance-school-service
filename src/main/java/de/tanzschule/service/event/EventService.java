package de.tanzschule.service.event;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventTimeRangeRepository eventTimeRangeRepository;

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return eventRepository.findAllByOrderByDateAscDisplayOrderAsc().stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse findById(UUID id) {
        Event event = eventRepository.findWithTimeRangesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        Event event = new Event(
                request.name(),
                request.date(),
                request.entryCost(),
                request.entryCostWithCustomerCard(),
                request.remark()
        );
        event = eventRepository.save(event);

        if (request.timeRanges() != null) {
            for (EventTimeRangeRequest timeRangeRequest : request.timeRanges()) {
                EventTimeRange timeRange = new EventTimeRange(
                        timeRangeRequest.startTime(),
                        timeRangeRequest.endTime(),
                        event
                );
                eventTimeRangeRepository.save(timeRange);
            }
        }

        Event saved = eventRepository.findWithTimeRangesById(event.getId()).orElseThrow();
        return EventResponse.from(saved);
    }

    @Transactional
    public EventResponse update(UUID id, EventRequest request) {
        Event event = eventRepository.findWithTimeRangesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));

        event.setName(request.name());
        event.setDate(request.date());
        event.setEntryCost(request.entryCost());
        event.setEntryCostWithCustomerCard(request.entryCostWithCustomerCard());
        event.setRemark(request.remark());
        event.setUpdatedAt(LocalDateTime.now());

        eventTimeRangeRepository.deleteAllByEventId(id);

        if (request.timeRanges() != null) {
            for (EventTimeRangeRequest timeRangeRequest : request.timeRanges()) {
                EventTimeRange timeRange = new EventTimeRange(
                        timeRangeRequest.startTime(),
                        timeRangeRequest.endTime(),
                        event
                );
                eventTimeRangeRepository.save(timeRange);
            }
        }

        Event saved = eventRepository.findWithTimeRangesById(event.getId()).orElseThrow();
        return EventResponse.from(saved);
    }

    @Transactional
    public List<EventResponse> reorder(List<UUID> orderedIds) {
        List<Event> events = eventRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID eventId = orderedIds.get(i);
            Event event = events.stream()
                    .filter(e -> e.getId().equals(eventId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Event with id " + eventId + " not found"));
            event.setDisplayOrder(i);
            event.setUpdatedAt(LocalDateTime.now());
        }
        return eventRepository.saveAll(events).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        Event event = eventRepository.findWithTimeRangesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
        eventTimeRangeRepository.deleteAllByEventId(id);
        eventRepository.delete(event);
    }
}
