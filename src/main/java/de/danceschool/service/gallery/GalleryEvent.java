package de.danceschool.service.gallery;

import de.danceschool.service.common.BaseEntity;
import de.danceschool.service.image.Image;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gallery_event")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GalleryEvent extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "galleryEvent")
    @OrderBy("displayOrder ASC")
    private List<Image> images = new ArrayList<>();

    public GalleryEvent(String name, LocalDate date) {
        super();
        this.name = name;
        this.date = date;
    }
}
