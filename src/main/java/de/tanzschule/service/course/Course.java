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

@Entity
@Table(name = "course")
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

    protected Course() {
    }

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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(String numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isPartnerOption() {
        return partnerOption;
    }

    public void setPartnerOption(boolean partnerOption) {
        this.partnerOption = partnerOption;
    }

    public CourseCategory getCategory() {
        return category;
    }

    public void setCategory(CourseCategory category) {
        this.category = category;
    }

    public List<CourseTariff> getTariffs() {
        return tariffs;
    }
}
