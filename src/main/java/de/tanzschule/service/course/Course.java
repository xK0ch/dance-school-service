package de.tanzschule.service.course;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Course extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "number_of_hours", nullable = false, length = 100)
    private String numberOfHours;

    @Column(nullable = false)
    private String teacher;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "partner_option", nullable = false)
    private boolean partnerOption;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CourseCategory category;

    @OneToMany(mappedBy = "course")
    @OrderBy("id ASC")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<CourseTariff> tariffs = new ArrayList<>();

    public Course(String name, LocalDate startDate, LocalTime startTime, LocalTime endTime,
                  String numberOfHours, String teacher, String remark, boolean partnerOption,
                  CourseCategory category) {
        super();
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfHours = numberOfHours;
        this.teacher = teacher;
        this.remark = remark;
        this.partnerOption = partnerOption;
        this.category = category;
    }
}
