package de.danceschool.service.image;

import de.danceschool.service.common.BaseResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record ImageResponse(
        @NotNull UUID id,
        @NotNull String filename,
        @NotNull String originalFilename,
        @NotNull String contentType,
        @NotNull Long fileSize,
        @NotNull Integer displayOrder,
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
