package de.danceschool.service.course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @EntityGraph(attributePaths = "tariffs")
    List<Course> findAllByCategoryIdOrderByDisplayOrderAsc(UUID categoryId);

    @EntityGraph(attributePaths = "tariffs")
    Optional<Course> findWithTariffsById(UUID id);
}
