package com.DXC.iotbackend.mapper;

import com.DXC.iotbackend.model.AirPollutionData;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.payload.AirPollutionReadingWithAlertDTO;
import com.DXC.iotbackend.util.AbstractSensorReadingMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AirPollutionMapper
        extends AbstractSensorReadingMapper<AirPollutionData, AirPollutionReadingWithAlertDTO>
        implements SensorMapper<AirPollutionData, AirPollutionReadingWithAlertDTO> {

    @Override
    protected String getSensorType() {
        return "Air_Pollution";
    }

    @Override
    protected String getLocation(AirPollutionData r) {
        return r.getLocation();
    }

    @Override
    protected String getStatus(AirPollutionData r) {
        return r.getPollutionLevel();
    }

    @Override
    protected LocalDateTime getTimestamp(AirPollutionData r) {
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
    protected AirPollutionReadingWithAlertDTO mapToDTO(AirPollutionData r, List<?> matchingAlerts) {
        List<Alert> alerts = matchingAlerts.stream().map(a -> (Alert) a).toList();
        LocalDateTime alertTime = alerts.isEmpty() ? null : alerts.get(0).getTimestamp();

        return new AirPollutionReadingWithAlertDTO(
                r.getId(),
                r.getLocation(),
                r.getTimestamp(),
                r.getCo(),
                r.getOzone(),
                r.getPollutionLevel(),
                alertTime,
                alerts
        );
    }

    @Override
    protected Comparator<AirPollutionReadingWithAlertDTO> getComparator(String sortBy) {
        return switch (sortBy) {
            case "co" -> Comparator.comparing(AirPollutionReadingWithAlertDTO::getCo);
            case "ozone" -> Comparator.comparing(AirPollutionReadingWithAlertDTO::getOzone);
            case "alertTimestamp" -> Comparator.comparing(dto -> Optional.ofNullable(dto.getAlertTimestamp()).orElse(LocalDateTime.MIN));
            case "timestamp" -> Comparator.comparing(AirPollutionReadingWithAlertDTO::getTimestamp);
            default -> null;
        };
    }
}