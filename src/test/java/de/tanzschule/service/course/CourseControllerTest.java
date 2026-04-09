package de.tanzschule.service.course;

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

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private CourseResponse sampleResponse;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = new CourseResponse(
                id, "Welttanzprogramm Teil 1",
                LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 45),
                LocalTime.of(21, 30),
                "8 Doppelstunden", "Uwe Höftmann", null, true, 0, categoryId,
                List.of(),
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsCourse() throws Exception {
        when(courseService.findById(eq(id))).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/courses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Welttanzprogramm Teil 1"))
                .andExpect(jsonPath("$.teacher").value("Uwe Höftmann"))
                .andExpect(jsonPath("$.partnerOption").value(true));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(courseService.findById(eq(nonExistingId))).thenThrow(new ResourceNotFoundException("Course with id " + nonExistingId + " not found"));

        mockMvc.perform(get("/api/courses/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        CourseRequest request = new CourseRequest(
                "Discofox", LocalDate.of(2026, 6, 1),
                LocalTime.of(20, 0), LocalTime.of(21, 0),
                "4 Stunden", "Tabea Höftmann", null, false, categoryId,
                List.of(new CourseTariffRequest("Normal", new BigDecimal("78.00")))
        );
        when(courseService.create(any(CourseRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Welttanzprogramm Teil 1"));
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        CourseRequest request = new CourseRequest(
                "Updated", LocalDate.of(2026, 7, 1),
                LocalTime.of(18, 0), LocalTime.of(19, 30),
                "6 Doppelstunden", "Uwe Höftmann", "Remark", true, categoryId, List.of()
        );
        when(courseService.update(eq(id), any(CourseRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/courses/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(courseService).delete(eq(id));

        mockMvc.perform(delete("/api/courses/" + id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        CourseRequest request = new CourseRequest(
                "", null, null, null, "", "", null, null, null, null
        );

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
