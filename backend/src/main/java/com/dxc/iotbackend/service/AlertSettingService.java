package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.*;
import com.dxc.iotbackend.repository.AlertSettingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AlertSettingService {

    private static final String ABOVE = "above";
    private static final String BELOW = "below";

    private static final String TRAFFIC = "Traffic";
    private static final String AIR_POLLUTION = "Air_Pollution";
    private static final String STREET_LIGHT = "Street_Light";

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

    private static final Map<String, float[]> METRIC_RANGES = Map.ofEntries(
            Map.entry(TRAFFIC_DENSITY, new float[]{0f, 500f}),
            Map.entry(AVG_SPEED, new float[]{0f, 120f}),
            Map.entry(CO, new float[]{0f, 50f}),
            Map.entry(OZONE, new float[]{0f, 300f}),
            Map.entry(BRIGHTNESS_LEVEL, new float[]{0f, 100f}),
            Map.entry(POWER_CONSUMPTION, new float[]{0f, 5000f})
    );

    private final AlertSettingRepository repository;

    public AlertSettingService(AlertSettingRepository repository) {
        this.repository = repository;
    }

    public AlertSetting save(AlertSetting setting) {
        validateMetricForType(setting.getType(), setting.getMetric());
        validateThresholdValue(setting.getMetric(), setting.getThresholdValue());
        return repository.save(setting);
    }

    private void validateMetricForType(String type, String metric) {
        List<String> allowedMetrics = VALID_METRICS.get(type);
        if (allowedMetrics == null || !allowedMetrics.contains(metric)) {
            throw new IllegalArgumentException("Invalid metric '" + metric + "' for type '" + type + "'");
        }
    }

    private void validateThresholdValue(String metric, Float value) {
        if (value == null) {
            throw new IllegalArgumentException("Threshold value cannot be null");
        }
        float[] range = METRIC_RANGES.get(metric);
        if (range == null || value < range[0] || value > range[1]) {
            throw new IllegalArgumentException(
                metric + " must be between " + range[0] + " and " + range[1]
            );
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
            if (!setting.getType().equalsIgnoreCase(type)) {
                continue;
            }

            float actualValue = resolveValue(setting.getMetric(), value1, value2);
            if (actualValue == -1f) {
                continue;
            }

            if (isThresholdViolated(actualValue, setting.getThresholdValue(), setting.getAlertType())) {
                alerts.add(createAlert(location, type, setting, actualValue));
            }
        }

        return alerts;
    }

    private float resolveValue(String metric, float value1, float value2) {
        return switch (metric) {
            case TRAFFIC_DENSITY, CO, BRIGHTNESS_LEVEL -> value1;
            case AVG_SPEED, OZONE, POWER_CONSUMPTION -> value2;
            default -> -1f;
        };
    }

    private boolean isThresholdViolated(float actual, float threshold, String alertType) {
        return switch (alertType) {
            case ABOVE -> actual > threshold;
            case BELOW -> actual < threshold;
            default -> false;
        };
    }

    private Alert createAlert(String location, String type, AlertSetting setting, float value) {
        Alert alert = new Alert();
        alert.setType(type);
        alert.setMetric(setting.getMetric());
        alert.setLocation(location);
        alert.setValue(value);
        alert.setTimestamp(LocalDateTime.now());
        alert.setAlertType(setting.getAlertType());
        alert.setMessage(buildAlertMessage(alert, setting.getThresholdValue()));
        return alert;
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
        String direction = ABOVE.equals(alert.getAlertType()) ? "exceeded" : "dropped below";
        return String.format(
                "%s at %s has %s the threshold of %.2f",
                metricLabel, alert.getLocation(), direction, threshold
        );
    }

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
