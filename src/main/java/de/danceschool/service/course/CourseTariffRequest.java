package de.danceschool.service.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CourseTariffRequest(
        @NotBlank(message = "Tariff name must not be blank")
        String name,

        @NotNull(message = "Price must not be null")
        BigDecimal price
) {
}
