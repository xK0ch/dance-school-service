package de.tanzschule.service.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Get all events", description = "Returns all events ordered by date and display order")
    @SecurityRequirements
    public List<EventResponse> getAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Returns a single event with its time ranges")
    @SecurityRequirements
    public EventResponse getById(@PathVariable UUID id) {
        return eventService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create event", description = "Create a new event with time ranges (requires authentication)")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.create(request);
        return ResponseEntity.created(URI.create("/api/events/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Update an existing event and its time ranges (requires authentication)")
    public EventResponse update(@PathVariable UUID id, @Valid @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Delete an event and all its time ranges (requires authentication)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder events", description = "Reorder events by providing a list of IDs in the desired order (requires authentication)")
    public List<EventResponse> reorder(@RequestBody List<UUID> orderedIds) {
        return eventService.reorder(orderedIds);
    }
}
