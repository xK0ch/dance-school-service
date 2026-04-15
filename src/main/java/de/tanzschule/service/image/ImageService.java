package de.tanzschule.service.image;

import de.tanzschule.service.exception.ResourceNotFoundException;
import de.tanzschule.service.gallery.GalleryEvent;
import de.tanzschule.service.news.News;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final ImageRepository imageRepository;
    private final Path uploadDir;

    public ImageService(
            ImageRepository imageRepository,
            @Value("${gallery.upload-dir}") String uploadDir) {
        this.imageRepository = imageRepository;
        this.uploadDir = Path.of(uploadDir);
    }

    public Image findById(UUID id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with id " + id + " not found"));
    }

    public List<Image> findByGalleryEventId(UUID galleryEventId) {
        return imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(galleryEventId);
    }

    @Transactional
    public Image upload(MultipartFile file, GalleryEvent galleryEvent) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: JPEG, PNG, GIF, WebP");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String filename = UUID.randomUUID() + extension;

        int nextOrder = imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(galleryEvent.getId()).size();

        Files.createDirectories(uploadDir);
        Path targetPath = uploadDir.resolve(filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        Image image = new Image(
                filename,
                originalFilename != null ? originalFilename : filename,
                contentType,
                file.getSize(),
                nextOrder
        );
        image.setGalleryEvent(galleryEvent);
        return imageRepository.save(image);
    }

    @Transactional
    public Image upload(MultipartFile file, News news) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: JPEG, PNG, GIF, WebP");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String filename = UUID.randomUUID() + extension;

        int nextOrder = imageRepository.findByNewsIdOrderByDisplayOrderAsc(news.getId()).size();

        Files.createDirectories(uploadDir);
        Path targetPath = uploadDir.resolve(filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        Image image = new Image(
                filename,
                originalFilename != null ? originalFilename : filename,
                contentType,
                file.getSize(),
                nextOrder
        );
        image.setNews(news);
        return imageRepository.save(image);
    }

    public List<Image> findByNewsId(UUID newsId) {
        return imageRepository.findByNewsIdOrderByDisplayOrderAsc(newsId);
    }

    @Transactional
    public void deleteAllByNews(News news) throws IOException {
        List<Image> images = imageRepository.findByNewsIdOrderByDisplayOrderAsc(news.getId());
        for (Image image : images) {
            Path filePath = uploadDir.resolve(image.getFilename());
            Files.deleteIfExists(filePath);
        }
        imageRepository.deleteAll(images);
    }

    @Transactional
    public void delete(UUID id) throws IOException {
        Image image = findById(id);
        Path filePath = uploadDir.resolve(image.getFilename());
        Files.deleteIfExists(filePath);
        imageRepository.delete(image);
    }

    @Transactional
    public void deleteAllByGalleryEvent(GalleryEvent galleryEvent) throws IOException {
        List<Image> images = imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(galleryEvent.getId());
        for (Image image : images) {
            Path filePath = uploadDir.resolve(image.getFilename());
            Files.deleteIfExists(filePath);
        }
        imageRepository.deleteAll(images);
    }

    @Transactional
    public List<Image> reorder(UUID galleryEventId, List<UUID> orderedIds) {
        List<Image> images = imageRepository.findAllById(orderedIds);
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID imageId = orderedIds.get(i);
            Image image = images.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Image with id " + imageId + " not found"));
            if (image.getGalleryEvent() == null || !image.getGalleryEvent().getId().equals(galleryEventId)) {
                throw new IllegalArgumentException("Image with id " + imageId + " does not belong to this gallery event");
            }
            image.setDisplayOrder(i);
            image.setUpdatedAt(LocalDateTime.now());
        }
        return imageRepository.saveAll(images);
    }

    public Resource loadAsResource(Image image) throws IOException {
        Path filePath = uploadDir.resolve(image.getFilename());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException("File not found: " + image.getFilename());
        }
        return resource;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
