package de.danceschool.service.common;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BaseResponse {

    UUID id();

    LocalDateTime createdAt();

    LocalDateTime updatedAt();
}
