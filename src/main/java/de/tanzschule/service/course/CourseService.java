package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseTariffRepository courseTariffRepository;

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        Course course = courseRepository.findWithTariffsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + id + " not found"));
        return CourseResponse.from(course);
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
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

        Course saved = courseRepository.findWithTariffsById(course.getId()).orElseThrow();
        return CourseResponse.from(saved);
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = courseRepository.findWithTariffsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + id + " not found"));

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

        Course saved = courseRepository.findWithTariffsById(course.getId()).orElseThrow();
        return CourseResponse.from(saved);
    }

    @Transactional
    public List<CourseResponse> reorder(List<Long> orderedIds) {
        List<Course> courses = courseRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            Long courseId = orderedIds.get(i);
            Course course = courses.stream()
                    .filter(c -> c.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Course with id " + courseId + " not found"));
            course.setDisplayOrder(i);
            course.setUpdatedAt(LocalDateTime.now());
        }
        return courseRepository.saveAll(courses).stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findWithTariffsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course with id " + id + " not found"));
        courseTariffRepository.deleteAllByCourseId(id);
        courseRepository.delete(course);
    }
}
