package de.danceschool.service.gallery;

import de.danceschool.service.common.BaseResponse;
import de.danceschool.service.image.ImageResponse;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GalleryEventResponse(
        @NotNull UUID id,
        @NotNull String name,
        @NotNull LocalDate date,
        @NotNull List<ImageResponse> images,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt
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
