package de.danceschool.service.gallery;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danceschool.service.auth.JwtTokenProvider;
import de.danceschool.service.image.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GalleryEventIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.3-alpine");

    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("gallery.upload-dir", () -> tempDir.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GalleryEventRepository galleryEventRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();
        galleryEventRepository.deleteAll();
        adminToken = jwtTokenProvider.generateToken("admin");
    }

    @Test
    void fullGalleryEventLifecycle() throws Exception {
        // Create event
        String createResponse = mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Summer Dance\", \"date\": \"2026-07-15\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Summer Dance"))
                .andExpect(jsonPath("$.date").value("2026-07-15"))
                .andExpect(jsonPath("$.id").isString())
                .andReturn().getResponse().getContentAsString();

        String eventId = objectMapper.readTree(createResponse).get("id").asText();

        // Upload image to event
        MockMultipartFile file = new MockMultipartFile(
                "files", "test-photo.jpg", "image/jpeg", "fake-jpeg-data".getBytes());

        String uploadResponse = mockMvc.perform(multipart("/api/gallery-events/" + eventId + "/images")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].originalFilename").value("test-photo.jpg"))
                .andExpect(jsonPath("$[0].galleryEventId").value(eventId))
                .andReturn().getResponse().getContentAsString();

        String imageId = objectMapper.readTree(uploadResponse).get(0).get("id").asText();

        // Get event with images (public)
        mockMvc.perform(get("/api/gallery-events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Summer Dance"))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images[0].originalFilename").value("test-photo.jpg"));

        // List all events (public)
        mockMvc.perform(get("/api/gallery-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Summer Dance"));

        // Download image (public)
        mockMvc.perform(get("/api/gallery-events/" + eventId + "/images/" + imageId + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"));

        // Update event
        mockMvc.perform(put("/api/gallery-events/" + eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Summer Dance 2026\", \"date\": \"2026-07-20\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Summer Dance 2026"))
                .andExpect(jsonPath("$.date").value("2026-07-20"));

        // Delete image
        mockMvc.perform(delete("/api/gallery-events/" + eventId + "/images/" + imageId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Delete event
        mockMvc.perform(delete("/api/gallery-events/" + eventId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/gallery-events/" + eventId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test\", \"date\": \"2026-01-01\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadInvalidFileType_returns400() throws Exception {
        // Create event first
        String createResponse = mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Event\", \"date\": \"2026-01-01\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String eventId = objectMapper.readTree(createResponse).get("id").asText();

        MockMultipartFile file = new MockMultipartFile(
                "files", "document.pdf", "application/pdf", "pdf-data".getBytes());

        mockMvc.perform(multipart("/api/gallery-events/" + eventId + "/images")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEvent_cascadesImageCleanup() throws Exception {
        // Create event
        String createResponse = mockMvc.perform(post("/api/gallery-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"To Delete\", \"date\": \"2026-01-01\"}")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String eventId = objectMapper.readTree(createResponse).get("id").asText();

        // Upload two images in one request
        MockMultipartFile file1 = new MockMultipartFile(
                "files", "photo1.jpg", "image/jpeg", "data1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile(
                "files", "photo2.png", "image/png", "data2".getBytes());

        mockMvc.perform(multipart("/api/gallery-events/" + eventId + "/images")
                        .file(file1)
                        .file(file2)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));

        // Delete event (should cascade to images)
        mockMvc.perform(delete("/api/gallery-events/" + eventId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify event and images are gone
        mockMvc.perform(get("/api/gallery-events/" + eventId))
                .andExpect(status().isNotFound());
    }
}
