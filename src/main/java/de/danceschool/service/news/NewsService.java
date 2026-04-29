package de.danceschool.service.news;

import de.danceschool.service.exception.ResourceNotFoundException;
import de.danceschool.service.image.Image;
import de.danceschool.service.image.ImageService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final ImageService imageService;

    public List<News> findAll() {
        return newsRepository.findAllByOrderByDisplayOrderAsc();
    }

    public News findById(UUID id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News with id " + id + " not found"));
    }

    public Image findImageByNewsId(UUID newsId) {
        List<Image> images = imageService.findByNewsId(newsId);
        return images.isEmpty() ? null : images.getFirst();
    }

    @Transactional
    public News create(NewsRequest request) {
        News news = new News(request.title(), request.description(), request.displayOrder());
        return newsRepository.save(news);
    }

    @Transactional
    public News update(UUID id, NewsRequest request) {
        News news = findById(id);
        news.setTitle(request.title());
        news.setDescription(request.description());
        news.setDisplayOrder(request.displayOrder());
        news.setUpdatedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    @Transactional
    public void delete(UUID id) throws IOException {
        News news = findById(id);
        imageService.deleteAllByNews(news);
        newsRepository.delete(news);
    }

    @Transactional
    public List<News> reorder(List<UUID> orderedIds) {
        List<News> newsList = newsRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID newsId = orderedIds.get(i);
            News news = newsList.stream()
                    .filter(n -> n.getId().equals(newsId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("News with id " + newsId + " not found"));
            news.setDisplayOrder(i);
            news.setUpdatedAt(LocalDateTime.now());
        }
        return newsRepository.saveAll(newsList);
    }
}
