package de.tanzschule.service.faq;

import jakarta.validation.constraints.NotBlank;

public record FaqRequest(
        @NotBlank(message = "Question must not be blank")
        String question,

        @NotBlank(message = "Answer must not be blank")
        String answer
) {
}
