package de.tanzschule.service.event;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_cleanup_config")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class EventCleanupConfig extends BaseEntity {

    @Column(nullable = false)
    private boolean enabled;

    public EventCleanupConfig(boolean enabled) {
        super();
        this.enabled = enabled;
    }
}
