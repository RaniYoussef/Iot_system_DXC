package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.*;
import com.dxc.iotbackend.repository.AlertSettingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AlertSettingService {

    private static final String TRAFFIC = "Traffic";
    private static final String AIR_POLLUTION = "Air_Pollution";
    private static final String STREET_LIGHT = "Street_Light";
    private static final String ABOVE = "above";
    private static final String BELOW = "below";

    private static final String TRAFFIC_DENSITY = "trafficDensity";
    private static final String AVG_SPEED = "avgSpeed";
    private static final String CO = "co";
    private static final String OZONE = "ozone";
    private static final String BRIGHTNESS_LEVEL = "brightnessLevel";
    private static final String POWER_CONSUMPTION = "powerConsumption";

    private static final Map<String, List<String>> VALID_METRICS = Map.of(
        TRAFFIC, List.of(TRAFFIC_DENSITY, AVG_SPEED),
        AIR_POLLUTION, List.of(CO, OZONE),
        STREET_LIGHT, List.of(BRIGHTNESS_LEVEL, POWER_CONSUMPTION)
    );

    private final AlertSettingRepository repository;

    public AlertSettingService(AlertSettingRepository repository) {
        this.repository = repository;
    }

    public AlertSetting save(AlertSetting setting) {
        validateMetricForType(setting.getType(), setting.getMetric());
        validateThresholdValue(setting.getType(), setting.getMetric(), setting.getThresholdValue());
        return repository.save(setting);
    }

    private void validateMetricForType(String type, String metric) {
        List<String> allowedMetrics = VALID_METRICS.get(type);
        if (allowedMetrics == null || !allowedMetrics.contains(metric)) {
            throw new IllegalArgumentException("Invalid metric '" + metric + "' for type '" + type + "'");
        }
    }

    private void validateThresholdValue(String type, String metric, Float value) {
        if (value == null) throw new IllegalArgumentException("Threshold value cannot be null");
        switch (type) {
            case TRAFFIC -> {
                if (TRAFFIC_DENSITY.equals(metric) && (value < 0 || value > 500)) {
                    throw new IllegalArgumentException("trafficDensity must be between 0 and 500");
                }
                if (AVG_SPEED.equals(metric) && (value < 0 || value > 120)) {
                    throw new IllegalArgumentException("avgSpeed must be between 0 and 120");
                }
            }
            case AIR_POLLUTION -> {
                if (CO.equals(metric) && (value < 0 || value > 50)) {
                    throw new IllegalArgumentException("co must be between 0 and 50");
                }
                if (OZONE.equals(metric) && (value < 0 || value > 300)) {
                    throw new IllegalArgumentException("ozone must be between 0 and 300");
                }
            }
            case STREET_LIGHT -> {
                if (BRIGHTNESS_LEVEL.equals(metric) && (value < 0 || value > 100)) {
                    throw new IllegalArgumentException("brightnessLevel must be between 0 and 100");
                }
                if (POWER_CONSUMPTION.equals(metric) && (value < 0 || value > 5000)) {
                    throw new IllegalArgumentException("powerConsumption must be between 0 and 5000");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public List<Alert> checkThresholdViolations(TrafficTypeData data) {
        return generateAlerts(data.getLocation(), TRAFFIC, data.getTrafficDensity(), data.getAvgSpeed());
    }

    public List<Alert> checkThresholdViolations(AirPollutionData data) {
        return generateAlerts(data.getLocation(), AIR_POLLUTION, data.getCo(), data.getOzone());
    }

    public List<Alert> checkThresholdViolations(StreetLightData data) {
        return generateAlerts(data.getLocation(), STREET_LIGHT, data.getBrightnessLevel(), data.getPowerConsumption());
    }

    private List<Alert> generateAlerts(String location, String type, float value1, float value2) {
        List<Alert> alerts = new ArrayList<>();
        for (AlertSetting setting : repository.findAll()) {
            if (!setting.getType().equalsIgnoreCase(type)) continue;

            float actualValue = switch (setting.getMetric()) {
                case TRAFFIC_DENSITY, CO, BRIGHTNESS_LEVEL -> value1;
                case AVG_SPEED, OZONE, POWER_CONSUMPTION -> value2;
                default -> -1f;
            };

            if (actualValue == -1f) continue;

            boolean violated = switch (setting.getAlertType()) {
                case ABOVE -> actualValue > setting.getThresholdValue();
                case BELOW -> actualValue < setting.getThresholdValue();
                default -> false;
            };

            if (violated) {
                Alert alert = new Alert();
                alert.setType(type);
                alert.setMetric(setting.getMetric());
                alert.setLocation(location);
                alert.setValue(actualValue);
                alert.setTimestamp(LocalDateTime.now());
                alert.setAlertType(setting.getAlertType());
                alert.setMessage(buildAlertMessage(alert, setting.getThresholdValue()));
                alerts.add(alert);
            }
        }
        return alerts;
    }

    private String buildAlertMessage(Alert alert, float threshold) {
        String metricLabel = switch (alert.getMetric()) {
            case TRAFFIC_DENSITY -> "Traffic Density";
            case AVG_SPEED -> "Average Speed";
            case CO -> "CO Level";
            case OZONE -> "Ozone Level";
            case BRIGHTNESS_LEVEL -> "Brightness Level";
            case POWER_CONSUMPTION -> "Power Consumption";
            default -> "Metric";
        };
        String direction = alert.getAlertType().equals(ABOVE) ? "exceeded" : "dropped below";
        return String.format(
            "%s at %s has %s the threshold of %.2f",
            metricLabel, alert.getLocation(), direction, threshold
        );
    }

    // ✅ Re-added methods for controller compatibility

    public List<AlertSetting> getAll() {
        return repository.findAll();
    }

    public List<AlertSetting> getFiltered(String type, String metric) {
        return repository.findAll().stream()
            .filter(s -> type == null || s.getType().equalsIgnoreCase(type))
            .filter(s -> metric == null || s.getMetric().equalsIgnoreCase(metric))
            .toList();
    }
}
