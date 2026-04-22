package de.tanzschule.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.tanzschule.service.auth.JwtTokenProvider;
import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private EventResponse sampleResponse;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        EventTimeRangeResponse timeRange = new EventTimeRangeResponse(
                UUID.randomUUID(),
                LocalTime.of(20, 0),
                LocalTime.of(23, 0),
                id,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        sampleResponse = new EventResponse(
                id,
                "Tanzball",
                LocalDate.of(2026, 12, 31),
                new BigDecimal("15.00"),
                new BigDecimal("10.00"),
                "Silvesterball",
                0,
                List.of(timeRange),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void getAll_returnsAllEvents() throws Exception {
        when(eventService.findAll()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tanzball"))
                .andExpect(jsonPath("$[0].timeRanges[0].startTime").value("20:00:00"));
    }

    @Test
    void getById_existingId_returnsEvent() throws Exception {
        when(eventService.findById(eq(id))).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/events/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tanzball"))
                .andExpect(jsonPath("$.entryCost").value(15.00))
                .andExpect(jsonPath("$.entryCostWithCustomerCard").value(10.00));
    }

    @Test
    void getById_nonExistingId_returns404() throws Exception {
        when(eventService.findById(eq(nonExistingId)))
                .thenThrow(new ResourceNotFoundException("Event with id " + nonExistingId + " not found"));

        mockMvc.perform(get("/api/events/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        EventRequest request = new EventRequest(
                "Sommerfest",
                LocalDate.of(2026, 7, 15),
                new BigDecimal("12.00"),
                new BigDecimal("8.00"),
                "Open Air",
                List.of(new EventTimeRangeRequest(LocalTime.of(19, 0), LocalTime.of(22, 0)))
        );
        when(eventService.create(any(EventRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tanzball"));
    }

    @Test
    @WithMockUser
    void create_minimalRequest_returns201() throws Exception {
        // Optional fields (entryCost, entryCostWithCustomerCard, remark, timeRanges) can be null
        EventRequest request = new EventRequest(
                "Minimal Event",
                LocalDate.of(2026, 8, 1),
                null,
                null,
                null,
                null
        );
        when(eventService.create(any(EventRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        EventRequest request = new EventRequest(
                "Updated",
                LocalDate.of(2026, 9, 1),
                new BigDecimal("20.00"),
                null,
                null,
                List.of()
        );
        when(eventService.update(eq(id), any(EventRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/events/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(eventService).delete(eq(id));

        mockMvc.perform(delete("/api/events/" + id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        // Name blank and date null - should fail validation
        EventRequest request = new EventRequest(
                "", null, null, null, null, null
        );

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void reorder_authenticated_returns200() throws Exception {
        List<UUID> orderedIds = List.of(id, UUID.randomUUID());
        when(eventService.reorder(any())).thenReturn(List.of(sampleResponse));

        mockMvc.perform(put("/api/events/reorder")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderedIds)))
                .andExpect(status().isOk());
    }
}
