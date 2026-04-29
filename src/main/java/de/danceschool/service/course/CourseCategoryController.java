package de.danceschool.service.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/course-categories")
@Tag(name = "Course Categories", description = "Course category management")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @GetMapping
    @Operation(operationId = "getAllCourseCategories", summary = "Get course categories", description = "Returns course categories with their courses, sorted by display order. Optionally filter by category IDs.")
    @SecurityRequirements
    public List<CourseCategoryResponse> getAll(@RequestParam(required = false) List<UUID> ids) {
        if (ids != null && !ids.isEmpty()) {
            return courseCategoryService.findByIds(ids);
        }
        return courseCategoryService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getCourseCategoryById", summary = "Get course category by ID", description = "Returns a single course category with its courses")
    @SecurityRequirements
    public CourseCategoryResponse getById(@PathVariable UUID id) {
        return courseCategoryService.findById(id);
    }

    @PostMapping
    @Operation(operationId = "createCourseCategory", summary = "Create course category", description = "Create a new course category (requires authentication)")
    public ResponseEntity<CourseCategoryResponse> create(@Valid @RequestBody CourseCategoryRequest request) {
        CourseCategoryResponse response = courseCategoryService.create(request);
        return ResponseEntity.created(URI.create("/api/course-categories/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateCourseCategory", summary = "Update course category", description = "Update an existing course category (requires authentication)")
    public CourseCategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CourseCategoryRequest request) {
        return courseCategoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteCourseCategory", summary = "Delete course category", description = "Delete a course category (requires authentication)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @Operation(operationId = "reorderCourseCategories", summary = "Reorder course categories", description = "Reorder categories by providing a list of IDs in the desired order (requires authentication)")
    public List<CourseCategoryResponse> reorder(@RequestBody List<UUID> orderedIds) {
        return courseCategoryService.reorder(orderedIds);
    }
}
