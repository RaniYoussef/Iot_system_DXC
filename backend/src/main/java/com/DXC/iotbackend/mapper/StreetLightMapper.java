package com.dxc.iotbackend.mapper;

import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.dxc.iotbackend.util.AbstractSensorReadingMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StreetLightMapper extends AbstractSensorReadingMapper<StreetLightData, StreetLightReadingWithAlertDTO>
        implements SensorMapper<StreetLightData, StreetLightReadingWithAlertDTO> {
    @Override
    protected String getSensorType() {
        return "Street_Light";
    }

    @Override
    protected String getLocation(StreetLightData r) {
        return r.getLocation();
    }

    @Override
    protected String getStatus(StreetLightData r) {
        return r.getStatus();
    }

    @Override
    protected LocalDateTime getTimestamp(StreetLightData r) {
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
    protected StreetLightReadingWithAlertDTO mapToDTO(StreetLightData r, List<?> matchingAlerts) {
        List<Alert> alerts = matchingAlerts.stream().map(a -> (Alert) a).toList();
        LocalDateTime alertTime = alerts.isEmpty() ? null : alerts.get(0).getTimestamp();

        return new StreetLightReadingWithAlertDTO(
                r.getId(),
                r.getLocation(),
                r.getTimestamp(),
                r.getBrightnessLevel(),
                r.getPowerConsumption(),
                r.getStatus(),
                alertTime,
                alerts
        );
    }

    @Override
    protected Comparator<StreetLightReadingWithAlertDTO> getComparator(String sortBy) {
        return switch (sortBy) {
            case "brightnessLevel" -> Comparator.comparing(StreetLightReadingWithAlertDTO::getBrightnessLevel);
            case "powerConsumption" -> Comparator.comparing(StreetLightReadingWithAlertDTO::getPowerConsumption);
            case "alertTimestamp" -> Comparator.comparing(dto -> Optional.ofNullable(dto.getAlertTimestamp()).orElse(LocalDateTime.MIN));
            case "timestamp" -> Comparator.comparing(StreetLightReadingWithAlertDTO::getTimestamp);
            default -> null;
        };
    }
}