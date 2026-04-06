package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import java.time.LocalDateTime;
import java.util.List;

public record CourseCategoryResponse(
        Long id,
        String name,
        int displayOrder,
        List<CourseResponse> courses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseResponse {

    public static CourseCategoryResponse from(CourseCategory category) {
        return new CourseCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDisplayOrder(),
                category.getCourses().stream()
                        .map(CourseResponse::from)
                        .toList(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
