package de.tanzschule.service.faq;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FaqIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.3-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;

    @BeforeEach
    void setUp() {
        faqRepository.deleteAll();
        adminToken = jwtTokenProvider.generateToken("admin");
    }

    @Test
    void fullCrudLifecycle() throws Exception {
        // Create
        FaqRequest createRequest = new FaqRequest("What courses do you offer?", "We offer salsa, bachata, and more.", 0);

        String createResponse = mockMvc.perform(post("/api/faqs")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("What courses do you offer?"))
                .andExpect(jsonPath("$.id").isString())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(createResponse).get("id").asText();

        // Read all (public)
        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].question").value("What courses do you offer?"));

        // Read by id (public)
        mockMvc.perform(get("/api/faqs/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("We offer salsa, bachata, and more."));

        // Update
        FaqRequest updateRequest = new FaqRequest("What courses are available?", "Salsa, bachata, kizomba, and more.", 0);

        mockMvc.perform(put("/api/faqs/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("What courses are available?"));

        // Delete
        mockMvc.perform(delete("/api/faqs/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/faqs/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithoutAuth_returns401() throws Exception {
        FaqRequest request = new FaqRequest("Question?", "Answer.", 0);

        mockMvc.perform(post("/api/faqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAll_public_returns200() throws Exception {
        faqRepository.save(new Faq("Q1?", "A1.", 0));
        faqRepository.save(new Faq("Q2?", "A2.", 1));

        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
