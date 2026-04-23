package de.tanzschule.service.gallery;

import de.tanzschule.service.image.Image;
import de.tanzschule.service.image.ImageResponse;
import de.tanzschule.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gallery-events")
@Tag(name = "Gallery Events", description = "Gallery event and image management")
@RequiredArgsConstructor
public class GalleryEventController {

    private final GalleryEventService galleryEventService;
    private final ImageService imageService;

    @GetMapping
    @Operation(operationId = "getAllGalleryEvents", summary = "Get all gallery events", description = "Returns all gallery events sorted by date descending")
    @SecurityRequirements
    public List<GalleryEventResponse> getAll() {
        return galleryEventService.findAll().stream()
                .map(GalleryEventResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getGalleryEventById", summary = "Get gallery event by ID", description = "Returns a single gallery event with its images")
    @SecurityRequirements
    public GalleryEventResponse getById(@PathVariable UUID id) {
        return GalleryEventResponse.from(galleryEventService.findById(id));
    }

    @PostMapping
    @Operation(operationId = "createGalleryEvent", summary = "Create gallery event", description = "Create a new gallery event (requires authentication)")
    public ResponseEntity<GalleryEventResponse> create(@Valid @RequestBody GalleryEventRequest request) {
        GalleryEvent created = galleryEventService.create(request);
        GalleryEventResponse response = GalleryEventResponse.from(created);
        return ResponseEntity.created(URI.create("/api/gallery-events/" + created.getId())).body(response);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateGalleryEvent", summary = "Update gallery event", description = "Update an existing gallery event (requires authentication)")
    public GalleryEventResponse update(@PathVariable UUID id, @Valid @RequestBody GalleryEventRequest request) {
        return GalleryEventResponse.from(galleryEventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteGalleryEvent", summary = "Delete gallery event", description = "Delete a gallery event and all its images (requires authentication)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws IOException {
        galleryEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadGalleryImages", summary = "Upload images to gallery event", description = "Upload one or more images to a gallery event (requires authentication)")
    public ResponseEntity<List<ImageResponse>> uploadImages(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        GalleryEvent event = galleryEventService.findById(id);
        List<ImageResponse> responses = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            Image created = imageService.upload(file, event);
            responses.add(ImageResponse.from(created));
        }
        return ResponseEntity.status(201).body(responses);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @Operation(operationId = "deleteGalleryImage", summary = "Delete image from gallery event", description = "Delete a single image from a gallery event (requires authentication)")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id, @PathVariable UUID imageId) throws IOException {
        imageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/images/reorder")
    @Operation(operationId = "reorderGalleryImages", summary = "Reorder images in gallery event", description = "Reorder images by providing a list of IDs in the desired order (requires authentication)")
    public List<ImageResponse> reorderImages(@PathVariable UUID id, @RequestBody List<UUID> orderedIds) {
        return imageService.reorder(id, orderedIds).stream()
                .map(ImageResponse::from)
                .toList();
    }

    @GetMapping("/{id}/images/{imageId}/download")
    @Operation(operationId = "downloadGalleryImage", summary = "Download image", description = "Returns the image file for display or download")
    @SecurityRequirements
    public ResponseEntity<Resource> downloadImage(@PathVariable UUID id, @PathVariable UUID imageId) throws IOException {
        Image image = imageService.findById(imageId);
        Resource resource = imageService.loadAsResource(image);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFilename() + "\"")
                .body(resource);
    }
}
