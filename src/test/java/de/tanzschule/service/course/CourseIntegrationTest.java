package de.tanzschule.service.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tanzschule.service.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CourseIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseTariffRepository courseTariffRepository;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    void setUp() {
        courseTariffRepository.deleteAll();
        courseRepository.deleteAll();
        courseCategoryRepository.deleteAll();
        adminToken = jwtTokenProvider.generateToken("admin");
    }

    @Test
    void fullCrudLifecycle() throws Exception {
        // Create category
        CourseCategoryRequest categoryRequest = new CourseCategoryRequest("Erwachsene", 0);

        String categoryResponse = mockMvc.perform(post("/api/course-categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Erwachsene"))
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(categoryResponse).get("id").asLong();

        // Create course with tariffs
        CourseRequest courseRequest = new CourseRequest(
                "Welttanzprogramm Teil 1",
                LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 45),
                LocalTime.of(21, 30),
                "8 Doppelstunden",
                "Uwe Höftmann",
                "Anfängerkurs für Paare",
                true,
                categoryId,
                List.of(
                        new CourseTariffRequest("Normal", new BigDecimal("78.00")),
                        new CourseTariffRequest("Wiederholer", new BigDecimal("65.00"))
                )
        );

        String courseResponse = mockMvc.perform(post("/api/courses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Welttanzprogramm Teil 1"))
                .andExpect(jsonPath("$.teacher").value("Uwe Höftmann"))
                .andExpect(jsonPath("$.partnerOption").value(true))
                .andExpect(jsonPath("$.tariffs.length()").value(2))
                .andExpect(jsonPath("$.tariffs[0].name").value("Normal"))
                .andExpect(jsonPath("$.tariffs[0].price").value(78.00))
                .andReturn().getResponse().getContentAsString();

        Long courseId = objectMapper.readTree(courseResponse).get("id").asLong();

        // Read course by ID (public)
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Welttanzprogramm Teil 1"))
                .andExpect(jsonPath("$.tariffs.length()").value(2));

        // Read categories with courses (public)
        mockMvc.perform(get("/api/course-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Erwachsene"))
                .andExpect(jsonPath("$[0].courses.length()").value(1))
                .andExpect(jsonPath("$[0].courses[0].name").value("Welttanzprogramm Teil 1"));

        // Update course (replaces tariffs)
        CourseRequest updateRequest = new CourseRequest(
                "WTP Teil 1 - Aktualisiert",
                LocalDate.of(2026, 5, 15),
                LocalTime.of(20, 0),
                LocalTime.of(21, 30),
                "10 Doppelstunden",
                "Tabea Höftmann",
                "Neuer Termin",
                true,
                categoryId,
                List.of(new CourseTariffRequest("Normal", new BigDecimal("85.00")))
        );

        mockMvc.perform(put("/api/courses/" + courseId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WTP Teil 1 - Aktualisiert"))
                .andExpect(jsonPath("$.tariffs.length()").value(1))
                .andExpect(jsonPath("$.tariffs[0].price").value(85.00));

        // Delete course
        mockMvc.perform(delete("/api/courses/" + courseId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourseWithoutAuth_returns401() throws Exception {
        CourseRequest request = new CourseRequest(
                "Test", LocalDate.of(2026, 5, 1),
                LocalTime.of(19, 0), LocalTime.of(20, 0),
                "4 Stunden", "Test", null, false, 1L, List.of()
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCategoriesPublic_returns200() throws Exception {
        courseCategoryRepository.save(new CourseCategory("Jugendliche", 0));
        courseCategoryRepository.save(new CourseCategory("Erwachsene", 1));

        mockMvc.perform(get("/api/course-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
