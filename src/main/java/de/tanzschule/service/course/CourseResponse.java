package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String name,
        LocalDate startDate,
        LocalTime startTime,
        LocalTime endTime,
        String numberOfHours,
        String teacher,
        String remark,
        boolean partnerOption,
        int displayOrder,
        UUID categoryId,
        List<CourseTariffResponse> tariffs,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
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
