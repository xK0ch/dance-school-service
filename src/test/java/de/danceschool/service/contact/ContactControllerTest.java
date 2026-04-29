package de.danceschool.service.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danceschool.service.auth.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendMessage_validRequest_returns200() throws Exception {
        doNothing().when(contactService).sendContactMessage(any());

        String json = objectMapper.writeValueAsString(
                new ContactRequest("Max", "max@example.com", "0123", "Hallo!"));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(contactService).sendContactMessage(any(ContactRequest.class));
    }

    @Test
    void sendMessage_missingName_returns400() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ContactRequest("", "max@example.com", null, "Hallo!"));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMessage_invalidEmail_returns400() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ContactRequest("Max", "not-an-email", null, "Hallo!"));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMessage_missingMessage_returns400() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ContactRequest("Max", "max@example.com", null, ""));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
