package de.tanzschule.service.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tanzschule.service.auth.JwtTokenProvider;
import de.tanzschule.service.exception.ResourceNotFoundException;
import de.tanzschule.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NewsService newsService;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();

    @Test
    @WithMockUser
    void getAll_returnsListOfNews() throws Exception {
        News news = new News("Summer Party", "Join us for dancing!", 0);
        when(newsService.findAll()).thenReturn(List.of(news));
        when(newsService.findImageByNewsId(any())).thenReturn(null);

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Summer Party"))
                .andExpect(jsonPath("$[0].description").value("Join us for dancing!"))
                .andExpect(jsonPath("$[0].image").isEmpty());
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsNews() throws Exception {
        News news = new News("Summer Party", "Join us for dancing!", 0);
        when(newsService.findById(eq(id))).thenReturn(news);
        when(newsService.findImageByNewsId(eq(id))).thenReturn(null);

        mockMvc.perform(get("/api/news/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Summer Party"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(newsService.findById(eq(nonExistingId)))
                .thenThrow(new ResourceNotFoundException("News with id " + nonExistingId + " not found"));

        mockMvc.perform(get("/api/news/" + nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        NewsRequest request = new NewsRequest("New Event", "Exciting news!", 0);
        News created = new News("New Event", "Exciting news!", 0);
        when(newsService.create(any(NewsRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Event"));
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        NewsRequest request = new NewsRequest("Updated", "Updated description.", 1);
        News updated = new News("Updated", "Updated description.", 1);
        when(newsService.update(eq(id), any(NewsRequest.class))).thenReturn(updated);
        when(newsService.findImageByNewsId(eq(id))).thenReturn(null);

        mockMvc.perform(put("/api/news/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(newsService).delete(eq(id));

        mockMvc.perform(delete("/api/news/" + id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        NewsRequest request = new NewsRequest("", "", 0);

        mockMvc.perform(post("/api/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
