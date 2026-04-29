package de.danceschool.service.course;

import de.danceschool.service.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseCategoryService {

    private final CourseCategoryRepository courseCategoryRepository;

    @Transactional(readOnly = true)
    public List<CourseCategoryResponse> findAll() {
        return courseCategoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(CourseCategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseCategoryResponse> findByIds(List<UUID> ids) {
        return courseCategoryRepository.findByIdInOrderByDisplayOrderAsc(ids).stream()
                .map(CourseCategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseCategoryResponse findById(UUID id) {
        CourseCategory category = courseCategoryRepository.findWithCoursesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + id + " not found"));
        return CourseCategoryResponse.from(category);
    }

    @Transactional
    public CourseCategoryResponse create(CourseCategoryRequest request) {
        CourseCategory category = new CourseCategory(request.name(), request.displayOrder());
        return CourseCategoryResponse.from(courseCategoryRepository.save(category));
    }

    @Transactional
    public CourseCategoryResponse update(UUID id, CourseCategoryRequest request) {
        CourseCategory category = courseCategoryRepository.findWithCoursesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + id + " not found"));
        category.setName(request.name());
        category.setDisplayOrder(request.displayOrder());
        category.setUpdatedAt(LocalDateTime.now());
        return CourseCategoryResponse.from(courseCategoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        CourseCategory category = courseCategoryRepository.findWithCoursesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + id + " not found"));
        courseCategoryRepository.delete(category);
    }

    @Transactional
    public List<CourseCategoryResponse> reorder(List<UUID> orderedIds) {
        List<CourseCategory> categories = courseCategoryRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID categoryId = orderedIds.get(i);
            CourseCategory category = categories.stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Course category with id " + categoryId + " not found"));
            category.setDisplayOrder(i);
            category.setUpdatedAt(LocalDateTime.now());
        }
        return courseCategoryRepository.saveAll(categories).stream()
                .map(CourseCategoryResponse::from)
                .toList();
    }
}
