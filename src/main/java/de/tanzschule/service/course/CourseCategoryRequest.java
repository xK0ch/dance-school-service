package de.tanzschule.service.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCategoryRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Display order must not be null")
        Integer displayOrder
) {
}
