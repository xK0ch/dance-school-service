package de.tanzschule.service.image;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, UUID> {

    List<Image> findByGalleryEventIdOrderByDisplayOrderAsc(UUID galleryEventId);

    List<Image> findByNewsIdOrderByDisplayOrderAsc(UUID newsId);
}
