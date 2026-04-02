package de.tanzschule.service.gallery;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryEventRepository extends JpaRepository<GalleryEvent, Long> {

    @EntityGraph(attributePaths = "images")
    List<GalleryEvent> findAllByOrderByDateDesc();

    @EntityGraph(attributePaths = "images")
    Optional<GalleryEvent> findWithImagesById(Long id);
}
