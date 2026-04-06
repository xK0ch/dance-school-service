package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseTariffRepository courseTariffRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         CourseTariffRepository courseTariffRepository) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseTariffRepository = courseTariffRepository;
    }

    public Course findById(Long id) {
        return courseRepository.findWithTariffsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + id + " not found"));
    }

    @Transactional
    public Course create(CourseRequest request) {
        CourseCategory category = courseCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + request.categoryId() + " not found"));

        Course course = new Course(
                request.name(),
                request.startDate(),
                request.startTime(),
                request.endTime(),
                request.numberOfHours(),
                request.teacher(),
                request.remark(),
                request.partnerOption(),
                category
        );
        course = courseRepository.save(course);

        if (request.tariffs() != null) {
            for (CourseTariffRequest tariffRequest : request.tariffs()) {
                CourseTariff tariff = new CourseTariff(tariffRequest.name(), tariffRequest.price(), course);
                courseTariffRepository.save(tariff);
            }
        }

        return courseRepository.findWithTariffsById(course.getId()).orElseThrow();
    }

    @Transactional
    public Course update(Long id, CourseRequest request) {
        Course course = findById(id);

        CourseCategory category = courseCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + request.categoryId() + " not found"));

        course.setName(request.name());
        course.setStartDate(request.startDate());
        course.setStartTime(request.startTime());
        course.setEndTime(request.endTime());
        course.setNumberOfHours(request.numberOfHours());
        course.setTeacher(request.teacher());
        course.setRemark(request.remark());
        course.setPartnerOption(request.partnerOption());
        course.setCategory(category);
        course.setUpdatedAt(LocalDateTime.now());

        courseTariffRepository.deleteAllByCourseId(id);

        if (request.tariffs() != null) {
            for (CourseTariffRequest tariffRequest : request.tariffs()) {
                CourseTariff tariff = new CourseTariff(tariffRequest.name(), tariffRequest.price(), course);
                courseTariffRepository.save(tariff);
            }
        }

        return courseRepository.findWithTariffsById(course.getId()).orElseThrow();
    }

    @Transactional
    public void delete(Long id) {
        Course course = findById(id);
        courseTariffRepository.deleteAllByCourseId(id);
        courseRepository.delete(course);
    }
}
