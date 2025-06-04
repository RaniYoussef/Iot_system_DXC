package com.DXC.iotbackend.mapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.util.AbstractSensorReadingMapper;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TrafficMapper extends AbstractSensorReadingMapper<TrafficTypeData, TrafficReadingWithAlertDTO>
        implements SensorMapper<TrafficTypeData, TrafficReadingWithAlertDTO> {

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
        return r.getCongestionLevel(); // status field in this context
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
        List<Alert> alerts = matchingAlerts.stream().map(a -> (Alert) a).toList();
        LocalDateTime alertTime = alerts.isEmpty() ? null : alerts.get(0).getTimestamp();

        return new TrafficReadingWithAlertDTO(
                r.getId(),
                r.getLocation(),
                r.getTimestamp(),
                r.getTrafficDensity(),
                r.getAvgSpeed(),
                r.getCongestionLevel(),
                alertTime,
                alerts
        );
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