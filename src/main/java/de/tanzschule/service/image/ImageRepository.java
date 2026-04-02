package de.tanzschule.service.image;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByGalleryEventIdOrderByDisplayOrderAsc(Long galleryEventId);
}
