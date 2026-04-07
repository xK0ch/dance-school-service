package de.tanzschule.service.course;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @EntityGraph(attributePaths = "tariffs")
    List<Course> findAllByCategoryIdOrderByDisplayOrderAsc(Long categoryId);

    @EntityGraph(attributePaths = "tariffs")
    Optional<Course> findWithTariffsById(Long id);
}
