package de.tanzschule.service.course;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {

    @EntityGraph(attributePaths = "courses")
    List<CourseCategory> findAllByOrderByDisplayOrderAsc();

    @EntityGraph(attributePaths = "courses")
    Optional<CourseCategory> findWithCoursesById(Long id);
}
