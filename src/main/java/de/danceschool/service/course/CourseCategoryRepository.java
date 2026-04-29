package de.danceschool.service.course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, UUID> {

    @EntityGraph(attributePaths = "courses")
    List<CourseCategory> findAllByOrderByDisplayOrderAsc();

    @EntityGraph(attributePaths = "courses")
    List<CourseCategory> findByIdInOrderByDisplayOrderAsc(List<UUID> ids);

    @EntityGraph(attributePaths = "courses")
    Optional<CourseCategory> findWithCoursesById(UUID id);
}
