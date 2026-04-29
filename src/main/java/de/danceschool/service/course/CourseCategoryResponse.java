package de.danceschool.service.course;

import de.danceschool.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CourseCategoryResponse(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull Integer displayOrder,
        @NotNull List<CourseResponse> courses,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
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
