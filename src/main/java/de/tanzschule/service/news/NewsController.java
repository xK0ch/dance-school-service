package de.tanzschule.service.news;

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
@RequestMapping("/api/news")
@Tag(name = "News", description = "News management")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final ImageService imageService;

    @GetMapping
    @Operation(operationId = "getAllNews", summary = "Get all news", description = "Returns all news sorted by display order")
    @SecurityRequirements
    public List<NewsResponse> getAll() {
        return newsService.findAll().stream()
                .map(news -> NewsResponse.from(news, newsService.findImageByNewsId(news.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getNewsById", summary = "Get news by ID", description = "Returns a single news entry by its ID")
    @SecurityRequirements
    public NewsResponse getById(@PathVariable UUID id) {
        News news = newsService.findById(id);
        return NewsResponse.from(news, newsService.findImageByNewsId(id));
    }

    @PostMapping
    @Operation(operationId = "createNews", summary = "Create news", description = "Create a new news entry (requires authentication)")
    public ResponseEntity<NewsResponse> create(@Valid @RequestBody NewsRequest request) {
        News created = newsService.create(request);
        NewsResponse response = NewsResponse.from(created, null);
        return ResponseEntity.created(URI.create("/api/news/" + created.getId())).body(response);
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateNews", summary = "Update news", description = "Update an existing news entry (requires authentication)")
    public NewsResponse update(@PathVariable UUID id, @Valid @RequestBody NewsRequest request) {
        News updated = newsService.update(id, request);
        return NewsResponse.from(updated, newsService.findImageByNewsId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteNews", summary = "Delete news", description = "Delete a news entry and its image (requires authentication)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws IOException {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadNewsImage", summary = "Upload news image", description = "Upload or replace the image of a news entry (requires authentication)")
    public ResponseEntity<ImageResponse> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        News news = newsService.findById(id);

        // Delete existing image if present
        Image existing = newsService.findImageByNewsId(id);
        if (existing != null) {
            imageService.delete(existing.getId());
        }

        Image created = imageService.upload(file, news);
        return ResponseEntity.status(201).body(ImageResponse.from(created));
    }

    @DeleteMapping("/{id}/image")
    @Operation(operationId = "deleteNewsImage", summary = "Delete news image", description = "Delete the image of a news entry (requires authentication)")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) throws IOException {
        Image image = newsService.findImageByNewsId(id);
        if (image != null) {
            imageService.delete(image.getId());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image/download")
    @Operation(operationId = "downloadNewsImage", summary = "Download news image", description = "Returns the image file for display or download")
    @SecurityRequirements
    public ResponseEntity<Resource> downloadImage(@PathVariable UUID id) throws IOException {
        Image image = newsService.findImageByNewsId(id);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = imageService.loadAsResource(image);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/reorder")
    @Operation(operationId = "reorderNews", summary = "Reorder news", description = "Reorder news by providing a list of IDs in the desired order (requires authentication)")
    public List<NewsResponse> reorder(@RequestBody List<UUID> orderedIds) {
        return newsService.reorder(orderedIds).stream()
                .map(news -> NewsResponse.from(news, newsService.findImageByNewsId(news.getId())))
                .toList();
    }
}
