package de.danceschool.service.gallery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.danceschool.service.auth.JwtTokenProvider;
import de.danceschool.service.exception.ResourceNotFoundException;
import de.danceschool.service.image.Image;
import de.danceschool.service.image.ImageService;
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
import java.util.UUID;

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

    private final UUID eventId = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();
    private final UUID imageId = UUID.randomUUID();

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
        when(galleryEventService.findById(eq(eventId))).thenReturn(event);

        mockMvc.perform(get("/api/gallery-events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Summer Party"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(galleryEventService.findById(eq(nonExistingId)))
                .thenThrow(new ResourceNotFoundException("Gallery event with id " + nonExistingId + " not found"));

        mockMvc.perform(get("/api/gallery-events/" + nonExistingId))
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
        when(galleryEventService.update(eq(eventId), any(GalleryEventRequest.class))).thenReturn(updated);

        String requestJson = objectMapper.writeValueAsString(
                new GalleryEventRequest("Updated Party", LocalDate.of(2026, 8, 20)));

        mockMvc.perform(put("/api/gallery-events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Party"));
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(galleryEventService).delete(eq(eventId));

        mockMvc.perform(delete("/api/gallery-events/" + eventId).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void uploadImages_authenticated_returns201() throws Exception {
        GalleryEvent event = new GalleryEvent("Summer Party", LocalDate.of(2026, 7, 15));
        when(galleryEventService.findById(eq(eventId))).thenReturn(event);

        Image created = new Image("abc.jpg", "photo.jpg", "image/jpeg", 15, 0);
        created.setGalleryEvent(event);
        when(imageService.upload(any(), any(GalleryEvent.class))).thenReturn(created);

        MockMultipartFile file = new MockMultipartFile(
                "files", "photo.jpg", "image/jpeg", "fake-image-data".getBytes());

        mockMvc.perform(multipart("/api/gallery-events/" + eventId + "/images").file(file).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].originalFilename").value("photo.jpg"));
    }

    @Test
    @WithMockUser
    void deleteImage_authenticated_returns204() throws Exception {
        doNothing().when(imageService).delete(eq(imageId));

        mockMvc.perform(delete("/api/gallery-events/" + eventId + "/images/" + imageId).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void downloadImage_returnsFile() throws Exception {
        Image image = new Image("abc.jpg", "photo.jpg", "image/jpeg", 1024, 0);
        when(imageService.findById(eq(imageId))).thenReturn(image);
        when(imageService.loadAsResource(image))
                .thenReturn(new ByteArrayResource("fake-image-data".getBytes()));

        mockMvc.perform(get("/api/gallery-events/" + eventId + "/images/" + imageId + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"photo.jpg\""));
    }
}
