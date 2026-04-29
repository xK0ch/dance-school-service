package de.danceschool.service.news;

import de.danceschool.service.exception.ResourceNotFoundException;
import de.danceschool.service.image.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private NewsService newsService;

    private News sampleNews;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        sampleNews = new News("Summer Dance Event", "Join us for a fun evening of dancing!", 0);
    }

    @Test
    void findAll_returnsAllNews() {
        when(newsRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(List.of(sampleNews));

        List<News> result = newsService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Summer Dance Event");
    }

    @Test
    void findById_existingId_returnsNews() {
        when(newsRepository.findById(id)).thenReturn(Optional.of(sampleNews));

        News result = newsService.findById(id);

        assertThat(result.getTitle()).isEqualTo("Summer Dance Event");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(newsRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.findById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
    }

    @Test
    void create_validRequest_savesNews() {
        NewsRequest request = new NewsRequest("New Event", "Exciting news!", 0);
        when(newsRepository.save(any(News.class))).thenAnswer(invocation -> invocation.getArgument(0));

        News result = newsService.create(request);

        assertThat(result.getTitle()).isEqualTo("New Event");
        assertThat(result.getDescription()).isEqualTo("Exciting news!");
        verify(newsRepository).save(any(News.class));
    }

    @Test
    void update_existingId_updatesNews() {
        when(newsRepository.findById(id)).thenReturn(Optional.of(sampleNews));
        when(newsRepository.save(any(News.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NewsRequest request = new NewsRequest("Updated Title", "Updated description.", 1);
        News result = newsService.update(id, request);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated description.");
    }

    @Test
    void delete_existingId_deletesNews() throws Exception {
        when(newsRepository.findById(id)).thenReturn(Optional.of(sampleNews));

        newsService.delete(id);

        verify(imageService).deleteAllByNews(sampleNews);
        verify(newsRepository).delete(sampleNews);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(newsRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reorder_emptyList_savesEmptyList() {
        when(newsRepository.findAllById(List.of())).thenReturn(List.of());
        when(newsRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<News> result = newsService.reorder(List.of());

        assertThat(result).isEmpty();
        verify(newsRepository).saveAll(any());
    }
}
