package de.tanzschule.service.gallery;

import de.tanzschule.service.common.BaseResponse;
import de.tanzschule.service.image.ImageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GalleryEventResponse(
        UUID id,
        String name,
        LocalDate date,
        List<ImageResponse> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseResponse {

    public static GalleryEventResponse from(GalleryEvent event) {
        return new GalleryEventResponse(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getImages().stream()
                        .map(ImageResponse::from)
                        .toList(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
