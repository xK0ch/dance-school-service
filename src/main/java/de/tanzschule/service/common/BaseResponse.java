package de.tanzschule.service.common;

import java.time.LocalDateTime;

public interface BaseResponse {

    Long id();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();
}
