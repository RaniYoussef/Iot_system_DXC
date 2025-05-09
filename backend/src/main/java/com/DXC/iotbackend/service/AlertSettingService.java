package com.DXC.iotbackend.service;

import com.DXC.iotbackend.model.AlertSetting;
import com.DXC.iotbackend.repository.AlertSettingRepository;
import org.springframework.stereotype.Service;

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
}
