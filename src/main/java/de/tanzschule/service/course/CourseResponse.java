package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CourseResponse(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull LocalDate startDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull String numberOfHours,
        @NotNull String teacher,
        String remark,
        @NotNull Boolean partnerOption,
        @NotNull Integer displayOrder,
        @NotNull UUID categoryId,
        @NotNull List<CourseTariffResponse> tariffs,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getStartDate(),
                course.getStartTime(),
                course.getEndTime(),
                course.getNumberOfHours(),
                course.getTeacher(),
                course.getRemark(),
                course.isPartnerOption(),
                course.getDisplayOrder(),
                course.getCategory().getId(),
                course.getTariffs().stream()
                        .map(CourseTariffResponse::from)
                        .toList(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
