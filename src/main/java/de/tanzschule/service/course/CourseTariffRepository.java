package de.tanzschule.service.course;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTariffRepository extends JpaRepository<CourseTariff, UUID> {

    List<CourseTariff> findAllByCourseId(UUID courseId);

    void deleteAllByCourseId(UUID courseId);
}
