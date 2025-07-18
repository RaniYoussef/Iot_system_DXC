package com.dxc.iotbackend.service;

import com.DXC.iotbackend.model.*;
import com.DXC.iotbackend.repository.AlertSettingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlertSettingService {

    private final AlertSettingRepository repository;

    // Allowed metrics by type
    private static final Map<String, List<String>> VALID_METRICS = Map.of(
            "Traffic", List.of("trafficDensity", "avgSpeed"),
            "Air_Pollution", List.of("co", "ozone"),
            "Street_Light", List.of("brightnessLevel", "powerConsumption")
    );

    public AlertSettingService(AlertSettingRepository repository) {
        this.repository = repository;
    }

    public AlertSetting save(AlertSetting setting) {
        validateMetricForType(setting.getType(), setting.getMetric());
        validateThresholdValue(setting.getType(), setting.getMetric(), setting.getThresholdValue());
        return repository.save(setting);
    }

    public List<AlertSetting> getFiltered(String type, String metric) {
        List<AlertSetting> all = repository.findAll();
        return all.stream()
                .filter(s -> type == null || s.getType().equalsIgnoreCase(type))
                .filter(s -> metric == null || s.getMetric().equalsIgnoreCase(metric))
                .toList();
    }

    public List<AlertSetting> getAll() {
        return repository.findAll();
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
            case "Traffic" -> {
                if (metric.equals("trafficDensity") && (value < 0 || value > 500))
                    throw new IllegalArgumentException("trafficDensity must be between 0 and 500");
                if (metric.equals("avgSpeed") && (value < 0 || value > 120))
                    throw new IllegalArgumentException("avgSpeed must be between 0 and 120");
            }
            case "Air_Pollution" -> {
                if (metric.equals("co") && (value < 0 || value > 50))
                    throw new IllegalArgumentException("co must be between 0 and 50");
                if (metric.equals("ozone") && (value < 0 || value > 300))
                    throw new IllegalArgumentException("ozone must be between 0 and 300");
            }
            case "Street_Light" -> {
                if (metric.equals("brightnessLevel") && (value < 0 || value > 100))
                    throw new IllegalArgumentException("brightnessLevel must be between 0 and 100");
                if (metric.equals("powerConsumption") && (value < 0 || value > 5000))
                    throw new IllegalArgumentException("powerConsumption must be between 0 and 5000");
            }
        }
    }


    public List<Alert> checkThresholdViolations(AirPollutionData data) {
        List<AlertSetting> settings = repository.findAll();
        List<Alert> alerts = new ArrayList<>();

        for (AlertSetting setting : settings) {
            if (!setting.getType().equalsIgnoreCase("Air_Pollution")) continue;

            float actualValue = switch (setting.getMetric()) {
                case "co" -> data.getCo();
                case "ozone" -> data.getOzone();
                default -> -1f;
            };

            if (actualValue == -1f) continue;

            boolean isViolated = switch (setting.getAlertType()) {
                case "above" -> actualValue > setting.getThresholdValue();
                case "below" -> actualValue < setting.getThresholdValue();
                default -> false;
            };

            if (isViolated) {
                Alert alert = new Alert();
                alert.setType(setting.getType());
                alert.setMetric(setting.getMetric());
                alert.setLocation(data.getLocation());
                alert.setValue(actualValue);
                alert.setTimestamp(data.getTimestamp());
                alert.setAlertType(setting.getAlertType()); 
                alerts.add(alert);
                alert.setMessage(buildAlertMessage(alert, setting.getThresholdValue()));
            }
        }

        return alerts;
    }

    public List<Alert> checkThresholdViolations(TrafficTypeData data) {
        List<Alert> alerts = new ArrayList<>();

        for (AlertSetting setting : repository.findAll()) {
            if (!setting.getType().equalsIgnoreCase("Traffic")) continue;

            float actualValue = switch (setting.getMetric()) {
                case "trafficDensity" -> data.getTrafficDensity();
                case "avgSpeed" -> data.getAvgSpeed();
                default -> -1f;
            };

            if (actualValue == -1f) continue;

            boolean violated = switch (setting.getAlertType()) {
                case "above" -> actualValue > setting.getThresholdValue();
                case "below" -> actualValue < setting.getThresholdValue();
                default -> false;
            };

            if (violated) {
                Alert alert = new Alert();
                alert.setType("Traffic");
                alert.setMetric(setting.getMetric());
                alert.setLocation(data.getLocation());
                alert.setValue(actualValue);
                alert.setTimestamp(data.getTimestamp());
                alert.setAlertType(setting.getAlertType()); 
                alerts.add(alert);
                alert.setMessage(buildAlertMessage(alert, setting.getThresholdValue()));
            }
        }

        return alerts;
    }

    public List<Alert> checkThresholdViolations(StreetLightData data) {
        List<Alert> alerts = new ArrayList<>();

        for (AlertSetting setting : repository.findAll()) {
            if (!setting.getType().equalsIgnoreCase("Street_Light")) continue;

            float actualValue = switch (setting.getMetric()) {
                case "brightnessLevel" -> data.getBrightnessLevel();
                case "powerConsumption" -> data.getPowerConsumption();
                default -> -1f;
            };

            if (actualValue == -1f) continue;

            boolean violated = switch (setting.getAlertType()) {
                case "above" -> actualValue > setting.getThresholdValue();
                case "below" -> actualValue < setting.getThresholdValue();
                default -> false;
            };

            if (violated) {
                Alert alert = new Alert();
                alert.setType("Street_Light");
                alert.setMetric(setting.getMetric());
                alert.setLocation(data.getLocation());
                alert.setValue(actualValue);
                alert.setTimestamp(data.getTimestamp());
                alert.setAlertType(setting.getAlertType()); 
                alerts.add(alert);
                alert.setMessage(buildAlertMessage(alert, setting.getThresholdValue()));
            }
        }

        return alerts;
    }



    private String buildAlertMessage(Alert alert, float threshold) {
        String metricLabel = switch (alert.getMetric()) {
            case "trafficDensity" -> "Traffic Density";
            case "avgSpeed" -> "Average Speed";
            case "co" -> "CO Level";
            case "ozone" -> "Ozone Level";
            case "brightnessLevel" -> "Brightness Level";
            case "powerConsumption" -> "Power Consumption";
            default -> "Metric";
        };

        String direction = alert.getAlertType().equals("above") ? "exceeded" : "dropped below";

        return String.format(
                "🚨 %s %s threshold at %s" +
                        " Current: %.1f | Threshold: %.1f " +
                        "🕒 %s",
                metricLabel,
                direction,
                alert.getLocation(),
                alert.getValue(),
                threshold,
                alert.getTimestamp().toLocalTime().toString()
        );
    }

}

