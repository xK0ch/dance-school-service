package de.tanzschule.service.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewsRequest(
        @NotBlank(message = "Title must not be blank")
        String title,

        @NotBlank(message = "Description must not be blank")
        String description,

        @NotNull(message = "Display order must not be null")
        Integer displayOrder
) {
}
