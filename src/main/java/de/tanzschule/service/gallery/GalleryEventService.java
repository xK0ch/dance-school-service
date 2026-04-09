package de.tanzschule.service.gallery;

import de.tanzschule.service.exception.ResourceNotFoundException;
import de.tanzschule.service.image.ImageService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GalleryEventService {

    private final GalleryEventRepository galleryEventRepository;
    private final ImageService imageService;

    public List<GalleryEvent> findAll() {
        return galleryEventRepository.findAllByOrderByDateDesc();
    }

    public GalleryEvent findById(UUID id) {
        return galleryEventRepository.findWithImagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gallery event with id " + id + " not found"));
    }

    @Transactional
    public GalleryEvent create(GalleryEventRequest request) {
        GalleryEvent event = new GalleryEvent(request.name(), request.date());
        return galleryEventRepository.save(event);
    }

    @Transactional
    public GalleryEvent update(UUID id, GalleryEventRequest request) {
        GalleryEvent event = findById(id);
        event.setName(request.name());
        event.setDate(request.date());
        event.setUpdatedAt(LocalDateTime.now());
        return galleryEventRepository.save(event);
    }

    @Transactional
    public void delete(UUID id) throws IOException {
        GalleryEvent event = findById(id);
        imageService.deleteAllByGalleryEvent(event);
        galleryEventRepository.delete(event);
    }
}
