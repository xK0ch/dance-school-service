package de.tanzschule.service.auth;

public record LoginResponse(
        String token,
        String username
) {
}
