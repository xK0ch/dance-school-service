package de.tanzschule.service.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tanzschule.service.auth.JwtTokenProvider;
import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseRegistrationController.class)
class CourseRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseRegistrationService registrationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private CourseRegistrationRequest validRequest() {
        return new CourseRegistrationRequest(
                "Herr", "Max", "Mustermann", "01.01.1990",
                "Musterstraße 1", "12345 Musterstadt",
                "0123456789", "0171234567", "max@example.com",
                null, "Normal", false, null, null,
                false, null, null, null
        );
    }

    @Test
    void register_validRequest_returns200() throws Exception {
        doNothing().when(registrationService).register(eq(1L), any(CourseRegistrationRequest.class));

        mockMvc.perform(post("/api/courses/1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void register_nonExistingCourse_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Course with id 99 not found"))
                .when(registrationService).register(eq(99L), any(CourseRegistrationRequest.class));

        mockMvc.perform(post("/api/courses/99/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void register_invalidRequest_returns400() throws Exception {
        CourseRegistrationRequest invalid = new CourseRegistrationRequest(
                "", "", "", "", "", "", "", null, "", null, "", null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/courses/1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
