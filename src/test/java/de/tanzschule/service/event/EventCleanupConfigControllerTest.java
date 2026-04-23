package de.tanzschule.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.tanzschule.service.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventCleanupConfigController.class)
class EventCleanupConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private EventCleanupConfigService service;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private EventCleanupConfigResponse sampleResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleResponse = new EventCleanupConfigResponse(
                UUID.randomUUID(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    void getConfig_authenticated_returns200() throws Exception {
        when(service.getConfig()).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/event-cleanup-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser
    void updateConfig_authenticated_returns200() throws Exception {
        EventCleanupConfigRequest request = new EventCleanupConfigRequest(false);
        EventCleanupConfigResponse disabled = new EventCleanupConfigResponse(
                sampleResponse.id(), false, sampleResponse.createdAt(), sampleResponse.updatedAt()
        );
        when(service.updateConfig(any(EventCleanupConfigRequest.class))).thenReturn(disabled);

        mockMvc.perform(put("/api/event-cleanup-config")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }

}
