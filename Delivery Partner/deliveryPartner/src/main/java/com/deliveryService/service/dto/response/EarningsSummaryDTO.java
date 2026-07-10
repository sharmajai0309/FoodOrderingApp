package com.deliveryService.service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EarningsSummaryDTO {

    private Long driverId;
    private String driverName;

    private BigDecimal totalEarnings;
    private BigDecimal thisWeekEarnings;
    private BigDecimal thisMonthEarnings;

    private long totalDeliveries;
    private long thisMonthDeliveries;
}
