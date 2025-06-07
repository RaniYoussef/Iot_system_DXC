package com.DXC.iotbackend.mapper;

import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.util.AbstractSensorReadingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TrafficMapper extends AbstractSensorReadingMapper<TrafficTypeData, TrafficReadingWithAlertDTO>
        implements SensorMapper<TrafficTypeData, TrafficReadingWithAlertDTO> {

    private static final Logger logger = LoggerFactory.getLogger(TrafficMapper.class);

    @Override
    protected String getSensorType() {
        return "Traffic";
    }

    @Override
    protected String getLocation(TrafficTypeData r) {
        return r.getLocation();
    }

    @Override
    protected String getStatus(TrafficTypeData r) {
        return r.getCongestionLevel();
    }

    @Override
    protected LocalDateTime getTimestamp(TrafficTypeData r) {
        return r.getTimestamp();
    }

    @Override
    protected String getAlertType(Object a) {
        return ((Alert) a).getType();
    }

    @Override
    protected String getAlertLocation(Object a) {
        return ((Alert) a).getLocation();
    }

    @Override
    protected LocalDateTime getAlertTimestamp(Object a) {
        return ((Alert) a).getTimestamp();
    }

    @Override
    protected TrafficReadingWithAlertDTO mapToDTO(TrafficTypeData r, List<?> matchingAlerts) {
        logger.info("Mapping reading ID {} at {} with congestionLevel={} and checking {} matching alerts",
                r.getId(), r.getLocation(), r.getCongestionLevel(), matchingAlerts.size());

        List<Alert> alerts = matchingAlerts.stream().map(a -> (Alert) a).toList();
        alerts.forEach(alert -> logger.info("  -> Matching Alert: {}", alert));

        LocalDateTime alertTime = alerts.isEmpty() ? null : alerts.get(0).getTimestamp();

        TrafficReadingWithAlertDTO dto = new TrafficReadingWithAlertDTO(
                r.getId(),
                r.getLocation(),
                r.getTimestamp(),
                r.getTrafficDensity(),
                r.getAvgSpeed(),
                r.getCongestionLevel(),
                alertTime,
                alerts
        );

        logger.info("Mapped DTO: {}", dto);
        return dto;
    }

    @Override
    protected Comparator<TrafficReadingWithAlertDTO> getComparator(String sortBy) {
        return switch (sortBy) {
            case "trafficDensity" -> Comparator.comparing(TrafficReadingWithAlertDTO::getTrafficDensity);
            case "avgSpeed" -> Comparator.comparing(TrafficReadingWithAlertDTO::getAvgSpeed);
            case "alertTimestamp" -> Comparator.comparing(dto -> Optional.ofNullable(dto.getAlertTimestamp()).orElse(LocalDateTime.MIN));
            case "timestamp" -> Comparator.comparing(TrafficReadingWithAlertDTO::getTimestamp);
            default -> null;
        };
    }
}