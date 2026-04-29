package de.danceschool.service.gallery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryEventRepository extends JpaRepository<GalleryEvent, UUID> {

    @EntityGraph(attributePaths = "images")
    List<GalleryEvent> findAllByOrderByDateDesc();

    @EntityGraph(attributePaths = "images")
    Optional<GalleryEvent> findWithImagesById(UUID id);
}
