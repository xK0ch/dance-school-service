package de.danceschool.service.faq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FaqRequest(
        @NotBlank(message = "Question must not be blank")
        String question,

        @NotBlank(message = "Answer must not be blank")
        String answer,

        @NotNull(message = "Display order must not be null")
        Integer displayOrder
) {
}
