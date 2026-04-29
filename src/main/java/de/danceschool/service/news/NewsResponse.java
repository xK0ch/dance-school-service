package de.danceschool.service.news;

import de.danceschool.service.common.BaseResponse;
import de.danceschool.service.image.Image;
import de.danceschool.service.image.ImageResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponse(
        @NotNull UUID id,
        @NotNull String title,
        @NotNull String description,
        ImageResponse image,
        @NotNull Integer displayOrder,
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
