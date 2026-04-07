package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_category")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CourseCategory extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @OneToMany(mappedBy = "category")
    @OrderBy("displayOrder ASC")
    private List<Course> courses = new ArrayList<>();

    public CourseCategory(String name, int displayOrder) {
        super();
        this.name = name;
        this.displayOrder = displayOrder;
    }
}
