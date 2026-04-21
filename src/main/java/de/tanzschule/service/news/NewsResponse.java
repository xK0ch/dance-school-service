package de.tanzschule.service.news;

import de.tanzschule.service.common.BaseResponse;
import de.tanzschule.service.image.Image;
import de.tanzschule.service.image.ImageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponse(
        @NotNull UUID id,
        @NotNull String title,
        @NotNull String description,
        ImageResponse image,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int displayOrder,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static NewsResponse from(News news, Image image) {
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getDescription(),
                image != null ? ImageResponse.from(image) : null,
                news.getDisplayOrder(),
                news.getCreatedAt(),
                news.getUpdatedAt()
        );
    }
}
