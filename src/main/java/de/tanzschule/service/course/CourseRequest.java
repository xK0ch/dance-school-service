package de.tanzschule.service.course;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CourseRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Start date must not be null")
        LocalDate startDate,

        @NotNull(message = "Start time must not be null")
        LocalTime startTime,

        @NotNull(message = "End time must not be null")
        LocalTime endTime,

        @NotBlank(message = "Number of hours must not be blank")
        String numberOfHours,

        @NotBlank(message = "Teacher must not be blank")
        String teacher,

        String remark,

        @NotNull(message = "Partner option must not be null")
        Boolean partnerOption,

        @NotNull(message = "Category ID must not be null")
        Long categoryId,

        @Valid
        List<CourseTariffRequest> tariffs
) {
}
