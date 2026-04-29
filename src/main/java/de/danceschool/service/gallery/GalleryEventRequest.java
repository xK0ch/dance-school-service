package de.danceschool.service.gallery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GalleryEventRequest(
        @NotBlank(message = "Name must not be blank") String name,
        @NotNull(message = "Date must not be null") LocalDate date
) {
}
