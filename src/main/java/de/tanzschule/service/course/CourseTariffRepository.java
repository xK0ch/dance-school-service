package de.tanzschule.service.course;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTariffRepository extends JpaRepository<CourseTariff, Long> {

    List<CourseTariff> findAllByCourseId(Long courseId);

    void deleteAllByCourseId(Long courseId);
}
