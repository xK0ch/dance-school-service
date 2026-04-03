package de.tanzschule.service.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactRequest(
        @NotBlank(message = "Name must not be blank") String name,
        @NotBlank(message = "Email must not be blank") @Email(message = "Email must be valid") String email,
        String phone,
        @NotBlank(message = "Message must not be blank") String message
) {
}
