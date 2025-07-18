package com.dxc.iotbackend.mapper;

import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.dxc.iotbackend.util.AbstractSensorReadingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StreetLightMapper extends AbstractSensorReadingMapper<StreetLightData, StreetLightReadingWithAlertDTO>
        implements SensorMapper<StreetLightData, StreetLightReadingWithAlertDTO> {

    private static final Logger logger = LoggerFactory.getLogger(StreetLightMapper.class);

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
        logger.info("Mapping reading ID {} at {} with status={} and checking {} matching alerts",
                r.getId(), r.getLocation(), r.getStatus(), matchingAlerts.size());

        List<Alert> alerts = matchingAlerts.stream().map(a -> (Alert) a).toList();
        alerts.forEach(alert -> logger.info("  -> Matching Alert: {}", alert));

        LocalDateTime alertTime = alerts.isEmpty() ? null : alerts.get(0).getTimestamp();

        StreetLightReadingWithAlertDTO dto = new StreetLightReadingWithAlertDTO(
                r.getId(),
                r.getLocation(),
                r.getTimestamp(),
                r.getBrightnessLevel(),
                r.getPowerConsumption(),
                r.getStatus(),
                alertTime,
                alerts
        );

        logger.info("Mapped DTO: {}", dto);
        return dto;
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