package de.tanzschule.service.course;

import de.tanzschule.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseCategoryService {

    private final CourseCategoryRepository courseCategoryRepository;

    public CourseCategoryService(CourseCategoryRepository courseCategoryRepository) {
        this.courseCategoryRepository = courseCategoryRepository;
    }

    public List<CourseCategory> findAll() {
        return courseCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public CourseCategory findById(Long id) {
        return courseCategoryRepository.findWithCoursesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + id + " not found"));
    }

    @Transactional
    public CourseCategory create(CourseCategoryRequest request) {
        CourseCategory category = new CourseCategory(request.name(), request.displayOrder());
        return courseCategoryRepository.save(category);
    }

    @Transactional
    public CourseCategory update(Long id, CourseCategoryRequest request) {
        CourseCategory category = findById(id);
        category.setName(request.name());
        category.setDisplayOrder(request.displayOrder());
        category.setUpdatedAt(LocalDateTime.now());
        return courseCategoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        CourseCategory category = findById(id);
        courseCategoryRepository.delete(category);
    }

    @Transactional
    public List<CourseCategory> reorder(List<Long> orderedIds) {
        List<CourseCategory> categories = courseCategoryRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            Long categoryId = orderedIds.get(i);
            CourseCategory category = categories.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + categoryId + " not found"));
            category.setDisplayOrder(i);
            category.setUpdatedAt(LocalDateTime.now());
        }
        return courseCategoryRepository.saveAll(categories);
    }
}
