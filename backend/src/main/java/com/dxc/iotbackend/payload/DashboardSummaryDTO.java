package com.dxc.iotbackend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DashboardSummaryDTO {
    private long totalAlerts;
    private long trafficAlerts;
    private long airAlerts;
    private long lightAlerts;

    private String latestTrafficMetric;
    private LocalDateTime latestTrafficTimestamp;

    private String latestAirMetric;
    private LocalDateTime latestAirTimestamp;

    private String latestLightMetric;
    private LocalDateTime latestLightTimestamp;
}
