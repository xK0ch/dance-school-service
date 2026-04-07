package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_category")
public class CourseCategory extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @OneToMany(mappedBy = "category")
    @OrderBy("displayOrder ASC")
    private List<Course> courses = new ArrayList<>();

    protected CourseCategory() {
    }

    public CourseCategory(String name, int displayOrder) {
        super();
        this.name = name;
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
