package de.tanzschule.service.faq;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FaqController.class)
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FaqService faqService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void getAll_returnsListOfFaqs() throws Exception {
        Faq faq = new Faq("Question?", "Answer.", 0);
        when(faqService.findAll()).thenReturn(List.of(faq));

        mockMvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Question?"))
                .andExpect(jsonPath("$[0].answer").value("Answer."));
    }

    @Test
    @WithMockUser
    void getById_existingId_returnsFaq() throws Exception {
        Faq faq = new Faq("Question?", "Answer.", 0);
        when(faqService.findById(1L)).thenReturn(faq);

        mockMvc.perform(get("/api/faqs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Question?"));
    }

    @Test
    @WithMockUser
    void getById_nonExistingId_returns404() throws Exception {
        when(faqService.findById(99L)).thenThrow(new ResourceNotFoundException("FAQ with id 99 not found"));

        mockMvc.perform(get("/api/faqs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void create_authenticated_returns201() throws Exception {
        FaqRequest request = new FaqRequest("New?", "Yes.", 0);
        Faq created = new Faq("New?", "Yes.", 0);
        when(faqService.create(any(FaqRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/faqs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("New?"));
    }

    @Test
    @WithMockUser
    void update_authenticated_returns200() throws Exception {
        FaqRequest request = new FaqRequest("Updated?", "Updated.", 1);
        Faq updated = new Faq("Updated?", "Updated.", 1);
        when(faqService.update(eq(1L), any(FaqRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/faqs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("Updated?"));
    }

    @Test
    @WithMockUser
    void delete_authenticated_returns204() throws Exception {
        doNothing().when(faqService).delete(1L);

        mockMvc.perform(delete("/api/faqs/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void create_invalidRequest_returns400() throws Exception {
        FaqRequest request = new FaqRequest("", "", 0);

        mockMvc.perform(post("/api/faqs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
