package de.tanzschule.service.faq;

import de.tanzschule.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private FaqService faqService;

    private Faq sampleFaq;

    @BeforeEach
    void setUp() {
        sampleFaq = new Faq("What are the opening hours?", "We are open from 9 to 21.");
    }

    @Test
    void findAll_returnsAllFaqs() {
        when(faqRepository.findAll()).thenReturn(List.of(sampleFaq));

        List<Faq> result = faqService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getQuestion()).isEqualTo("What are the opening hours?");
    }

    @Test
    void findById_existingId_returnsFaq() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));

        Faq result = faqService.findById(1L);

        assertThat(result.getQuestion()).isEqualTo("What are the opening hours?");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(faqRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validRequest_savesFaq() {
        FaqRequest request = new FaqRequest("New question?", "New answer.");
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Faq result = faqService.create(request);

        assertThat(result.getQuestion()).isEqualTo("New question?");
        assertThat(result.getAnswer()).isEqualTo("New answer.");
        verify(faqRepository).save(any(Faq.class));
    }

    @Test
    void update_existingId_updatesFaq() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));
        when(faqRepository.save(any(Faq.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FaqRequest request = new FaqRequest("Updated question?", "Updated answer.");
        Faq result = faqService.update(1L, request);

        assertThat(result.getQuestion()).isEqualTo("Updated question?");
        assertThat(result.getAnswer()).isEqualTo("Updated answer.");
    }

    @Test
    void delete_existingId_deletesFaq() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(sampleFaq));

        faqService.delete(1L);

        verify(faqRepository).delete(sampleFaq);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(faqRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
