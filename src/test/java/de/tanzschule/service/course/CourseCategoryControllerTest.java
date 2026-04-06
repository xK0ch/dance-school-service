package de.tanzschule.service.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tanzschule.service.auth.JwtTokenProvider;
import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseCategoryController.class)
class CourseCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseCategoryService courseCategoryService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private CourseCategoryResponse sampleResponse(String name, int displayOrder) {
        return new CourseCategoryResponse(1L, name, displayOrder, List.of(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getAll_returnsListOfCategories() throws Exception {
        when(courseCategoryService.findAll()).thenReturn(List.of(sampleResponse("Erwachsene", 0)));

        mockMvc.perform(get("/api/course-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Erwachsene"));
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsCategory() throws Exception {
        when(courseCategoryService.findById(1L)).thenReturn(sampleResponse("Jugendliche", 1));

        mockMvc.perform(get("/api/course-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jugendliche"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(courseCategoryService.findById(99L)).thenThrow(new ResourceNotFoundException("Course category with id 99 not found"));

        mockMvc.perform(get("/api/course-categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        CourseCategoryRequest request = new CourseCategoryRequest("Kinder", 2);
        when(courseCategoryService.create(any(CourseCategoryRequest.class))).thenReturn(sampleResponse("Kinder", 2));

        mockMvc.perform(post("/api/course-categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Kinder"));
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        CourseCategoryRequest request = new CourseCategoryRequest("Senioren", 3);
        when(courseCategoryService.update(eq(1L), any(CourseCategoryRequest.class))).thenReturn(sampleResponse("Senioren", 3));

        mockMvc.perform(put("/api/course-categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Senioren"));
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(courseCategoryService).delete(1L);

        mockMvc.perform(delete("/api/course-categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        CourseCategoryRequest request = new CourseCategoryRequest("", null);

        mockMvc.perform(post("/api/course-categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
