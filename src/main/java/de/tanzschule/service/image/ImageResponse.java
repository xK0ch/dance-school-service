package de.tanzschule.service.image;

import de.tanzschule.service.common.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record ImageResponse(
        @NotNull UUID id,
        @NotNull String filename,
        @NotNull String originalFilename,
        @NotNull String contentType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long fileSize,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int displayOrder,
        UUID galleryEventId,
        UUID newsId,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
) implements BaseResponse {

    public static ImageResponse from(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getFilename(),
                image.getOriginalFilename(),
                image.getContentType(),
                image.getFileSize(),
                image.getDisplayOrder(),
                image.getGalleryEvent() != null ? image.getGalleryEvent().getId() : null,
                image.getNews() != null ? image.getNews().getId() : null,
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}
