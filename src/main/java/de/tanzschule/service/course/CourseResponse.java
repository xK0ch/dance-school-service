package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record CourseResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalTime startTime,
        LocalTime endTime,
        String numberOfHours,
        String teacher,
        String remark,
        boolean partnerOption,
        Long categoryId,
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
                course.getCategory().getId(),
                course.getTariffs().stream()
                        .map(CourseTariffResponse::from)
                        .toList(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
