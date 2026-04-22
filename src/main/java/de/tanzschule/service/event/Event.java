package de.tanzschule.service.event;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @Column(name = "entry_cost", precision = 10, scale = 2)
    private BigDecimal entryCost;

    @Column(name = "entry_cost_with_customer_card", precision = 10, scale = 2)
    private BigDecimal entryCostWithCustomerCard;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @OneToMany(mappedBy = "event")
    @OrderBy("startTime ASC")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<EventTimeRange> timeRanges = new ArrayList<>();

    public Event(String name, LocalDate date, BigDecimal entryCost, BigDecimal entryCostWithCustomerCard, String remark) {
        super();
        this.name = name;
        this.date = date;
        this.entryCost = entryCost;
        this.entryCostWithCustomerCard = entryCostWithCustomerCard;
        this.remark = remark;
    }
}
