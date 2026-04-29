package de.danceschool.service.image;

import de.danceschool.service.exception.ResourceNotFoundException;
import de.danceschool.service.gallery.GalleryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    private ImageService imageService;

    @TempDir
    Path tempDir;

    private GalleryEvent galleryEvent;

    private final UUID id = UUID.randomUUID();
    private final UUID nonExistingId = UUID.randomUUID();
    private final UUID galleryEventId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        imageService = new ImageService(imageRepository, tempDir.toString());
        galleryEvent = new GalleryEvent("Test Event", LocalDate.of(2026, 1, 15));
    }

    @Test
    void findById_existingId_returnsImage() {
        Image image = new Image("abc.jpg", "photo.jpg", "image/jpeg", 1024, 0);
        when(imageRepository.findById(id)).thenReturn(Optional.of(image));

        Image result = imageService.findById(id);

        assertThat(result.getOriginalFilename()).isEqualTo("photo.jpg");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(imageRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.findById(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistingId.toString());
    }

    @Test
    void findByGalleryEventId_returnsImages() {
        Image image = new Image("abc.jpg", "photo.jpg", "image/jpeg", 1024, 0);
        when(imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(galleryEventId)).thenReturn(List.of(image));

        List<Image> result = imageService.findByGalleryEventId(galleryEventId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getOriginalFilename()).isEqualTo("photo.jpg");
    }

    @Test
    void upload_validImage_savesFileAndEntity() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "fake-image-data".getBytes());
        when(imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(any())).thenReturn(List.of());
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.upload(file, galleryEvent);

        assertThat(result.getOriginalFilename()).isEqualTo("test-image.jpg");
        assertThat(result.getContentType()).isEqualTo("image/jpeg");
        assertThat(result.getFilename()).endsWith(".jpg");
        assertThat(result.getGalleryEvent()).isEqualTo(galleryEvent);
        assertThat(Files.exists(tempDir.resolve(result.getFilename()))).isTrue();
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void upload_emptyFile_throwsException() {
        MultipartFile file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> imageService.upload(file, galleryEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void upload_invalidContentType_throwsException() {
        MultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "pdf-data".getBytes());

        assertThatThrownBy(() -> imageService.upload(file, galleryEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    void delete_existingId_deletesFileAndEntity() throws IOException {
        Image image = new Image("abc.jpg", "photo.jpg", "image/jpeg", 1024, 0);
        when(imageRepository.findById(id)).thenReturn(Optional.of(image));

        Path filePath = tempDir.resolve("abc.jpg");
        Files.write(filePath, "fake-data".getBytes());

        imageService.delete(id);

        assertThat(Files.exists(filePath)).isFalse();
        verify(imageRepository).delete(image);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(imageRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void upload_pngImage_accepted() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", "png-data".getBytes());
        when(imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(any())).thenReturn(List.of());
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.upload(file, galleryEvent);

        assertThat(result.getContentType()).isEqualTo("image/png");
        assertThat(result.getFilename()).endsWith(".png");
    }

    @Test
    void upload_webpImage_accepted() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "image.webp", "image/webp", "webp-data".getBytes());
        when(imageRepository.findByGalleryEventIdOrderByDisplayOrderAsc(any())).thenReturn(List.of());
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.upload(file, galleryEvent);

        assertThat(result.getContentType()).isEqualTo("image/webp");
    }
}
