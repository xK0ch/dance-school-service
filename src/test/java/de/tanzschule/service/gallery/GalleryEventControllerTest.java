package de.tanzschule.service.gallery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.tanzschule.service.auth.JwtTokenProvider;
import de.tanzschule.service.exception.ResourceNotFoundException;
import de.tanzschule.service.image.Image;
import de.tanzschule.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GalleryEventController.class)
class GalleryEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GalleryEventService galleryEventService;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @WithMockUser
    void getAll_returnsListOfEvents() throws Exception {
        GalleryEvent event = new GalleryEvent("Summer Party", LocalDate.of(2026, 7, 15));
        when(galleryEventService.findAll()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/gallery-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Summer Party"))
                .andExpect(jsonPath("$[0].date").value("2026-07-15"));
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsEvent() throws Exception {
        GalleryEvent event = new GalleryEvent("Summer Party", LocalDate.of(2026, 7, 15));
        when(galleryEventService.findById(1L)).thenReturn(event);

        mockMvc.perform(get("/api/gallery-events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Summer Party"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(galleryEventService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Gallery event with id 99 not found"));

        mockMvc.perform(get("/api/gallery-events/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        GalleryEvent created = new GalleryEvent("Summer Party", LocalDate.of(2026, 7, 15));
        when(galleryEventService.create(any(GalleryEventRequest.class))).thenReturn(created);

        String requestJson = objectMapper.writeValueAsString(
                new GalleryEventRequest("Summer Party", LocalDate.of(2026, 7, 15)));

        mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Summer Party"));
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        String requestJson = objectMapper.writeValueAsString(
                new GalleryEventRequest("", null));

        mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        GalleryEvent updated = new GalleryEvent("Updated Party", LocalDate.of(2026, 8, 20));
        when(galleryEventService.update(eq(1L), any(GalleryEventRequest.class))).thenReturn(updated);

        String requestJson = objectMapper.writeValueAsString(
                new GalleryEventRequest("Updated Party", LocalDate.of(2026, 8, 20)));

        mockMvc.perform(put("/api/gallery-events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Party"));
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(galleryEventService).delete(1L);

        mockMvc.perform(delete("/api/gallery-events/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void uploadImage_authenticated_returns201() throws Exception {
        GalleryEvent event = new GalleryEvent("Summer Party", LocalDate.of(2026, 7, 15));
        when(galleryEventService.findById(1L)).thenReturn(event);

        Image created = new Image("abc.jpg", "photo.jpg", "image/jpeg", 15, 0);
        created.setGalleryEvent(event);
        when(imageService.upload(any(), any(GalleryEvent.class))).thenReturn(created);

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake-image-data".getBytes());

        mockMvc.perform(multipart("/api/gallery-events/1/images").file(file).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalFilename").value("photo.jpg"));
    }

    @Test
    @WithMockUser
    void deleteImage_authenticated_returns204() throws Exception {
        doNothing().when(imageService).delete(5L);

        mockMvc.perform(delete("/api/gallery-events/1/images/5").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void downloadImage_returnsFile() throws Exception {
        Image image = new Image("abc.jpg", "photo.jpg", "image/jpeg", 1024, 0);
        when(imageService.findById(5L)).thenReturn(image);
        when(imageService.loadAsResource(image))
                .thenReturn(new ByteArrayResource("fake-image-data".getBytes()));

        mockMvc.perform(get("/api/gallery-events/1/images/5/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"photo.jpg\""));
    }
}
