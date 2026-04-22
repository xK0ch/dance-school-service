package de.tanzschule.service.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EventRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull(message = "Date must not be null")
        LocalDate date,

        BigDecimal entryCost,

        BigDecimal entryCostWithCustomerCard,

        String remark,

        @Valid
        List<EventTimeRangeRequest> timeRanges
) {
}
