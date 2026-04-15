package de.tanzschule.service.image;

import de.tanzschule.service.common.BaseEntity;
import de.tanzschule.service.gallery.GalleryEvent;
import de.tanzschule.service.news.News;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String filename;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_event_id")
    private GalleryEvent galleryEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news;

    public Image(String filename, String originalFilename, String contentType, long fileSize, int displayOrder) {
        super();
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.displayOrder = displayOrder;
    }
}
