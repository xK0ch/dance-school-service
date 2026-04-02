package de.tanzschule.service.image;

import de.tanzschule.service.common.BaseResponse;
import java.time.LocalDateTime;

public record ImageResponse(
        Long id,
        String filename,
        String originalFilename,
        String contentType,
        long fileSize,
        int displayOrder,
        Long galleryEventId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
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
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }
}
