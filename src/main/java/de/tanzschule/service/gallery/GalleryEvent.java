package de.tanzschule.service.gallery;

import de.tanzschule.service.common.BaseEntity;
import de.tanzschule.service.image.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gallery_event")
public class GalleryEvent extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "galleryEvent")
    @OrderBy("displayOrder ASC")
    private List<Image> images = new ArrayList<>();

    protected GalleryEvent() {
    }

    public GalleryEvent(String name, LocalDate date) {
        super();
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Image> getImages() {
        return images;
    }
}
