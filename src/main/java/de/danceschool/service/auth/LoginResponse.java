package de.danceschool.service.auth;

import jakarta.validation.constraints.NotNull;

public record LoginResponse(
        @NotNull String token,
        @NotNull String username
) {
}
