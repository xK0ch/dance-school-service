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
import java.time.LocalTime;
import java.util.List;

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

    private CourseCategory sampleCategory;
    private Course sampleCourse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleCategory = new CourseCategory("Erwachsene", 0);
        sampleCourse = new Course(
                "Welttanzprogramm Teil 1",
                LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 45),
                LocalTime.of(21, 30),
                "8 Doppelstunden",
                "Uwe Höftmann",
                null,
                true,
                sampleCategory
        );
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsCourse() throws Exception {
        when(courseService.findById(1L)).thenReturn(sampleCourse);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Welttanzprogramm Teil 1"))
                .andExpect(jsonPath("$.teacher").value("Uwe Höftmann"))
                .andExpect(jsonPath("$.partnerOption").value(true));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(courseService.findById(99L)).thenThrow(new ResourceNotFoundException("Course with id 99 not found"));

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        CourseRequest request = new CourseRequest(
                "Discofox", LocalDate.of(2026, 6, 1),
                LocalTime.of(20, 0), LocalTime.of(21, 0),
                "4 Stunden", "Tabea Höftmann", null, false, 1L,
                List.of(new CourseTariffRequest("Normal", new BigDecimal("78.00")))
        );
        when(courseService.create(any(CourseRequest.class))).thenReturn(sampleCourse);

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
                "6 Doppelstunden", "Uwe Höftmann", "Remark", true, 1L, List.of()
        );
        when(courseService.update(eq(1L), any(CourseRequest.class))).thenReturn(sampleCourse);

        mockMvc.perform(put("/api/courses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(courseService).delete(1L);

        mockMvc.perform(delete("/api/courses/1")
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
