package com.DXC.iotbackend.service;



import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.TrafficSensorDataRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;

@Service
public class TrafficSensorDataService {

    private final TrafficSensorDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;

    public TrafficSensorDataService(
            TrafficSensorDataRepository repository,
            AlertSettingService alertSettingService,
            AlertRepository alertRepository,
            AlertEmailService alertEmailService
    ) {
        this.repository = repository;
        this.alertSettingService = alertSettingService;
        this.alertRepository = alertRepository;
        this.alertEmailService = alertEmailService;
    }

    public TrafficTypeData saveData(TrafficTypeData data) {
        TrafficTypeData saved = repository.save(data);

        List<Alert> alerts = alertSettingService.checkThresholdViolations(data);
        for (Alert alert : alerts) {
            alertRepository.save(alert);
            alertEmailService.sendAlertEmail(alert);
        }

        return saved;
    }

    public List<TrafficTypeData> getAllData() {
        return repository.findAll();
    }


    public Page<TrafficTypeData> getAllPaged(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<TrafficTypeData> getFilteredTrafficData(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        return repository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (location != null && !location.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("location"), location));
            }
            if (congestionLevel != null && !congestionLevel.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("congestionLevel"), congestionLevel));
            }
            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), start));
            }
            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), end));
            }

            return predicate;
        }, pageable);
    }


    public List<TrafficReadingWithAlertDTO> getTrafficReadingsWithAlertInfo(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {
        List<TrafficTypeData> readings = repository.findAll();
        List<Alert> alerts = alertRepository.findAll();

        List<TrafficReadingWithAlertDTO> result = readings.stream()
                .filter(r -> {
                    if (location == null || location.isBlank()) return true;
                    return location.equalsIgnoreCase(Optional.ofNullable(r.getLocation()).orElse(""));
                })
                .filter(r -> {
                    if (congestionLevel == null || congestionLevel.isBlank()) return true;
                    return congestionLevel.equalsIgnoreCase(Optional.ofNullable(r.getCongestionLevel()).orElse(""));
                })
                .filter(r -> start == null || Optional.ofNullable(r.getTimestamp()).map(ts -> !ts.isBefore(start)).orElse(false))
                .filter(r -> end == null || Optional.ofNullable(r.getTimestamp()).map(ts -> !ts.isAfter(end)).orElse(false))

                .map(r -> {
                    List<Alert> matchingAlerts = alerts.stream()
                            .filter(a -> a.getType().equalsIgnoreCase("Traffic"))
                            .filter(a -> a.getLocation().equalsIgnoreCase(r.getLocation()))
                            .filter(a -> a.getTimestamp().equals(r.getTimestamp()))
                            .toList();

                    LocalDateTime alertTime = matchingAlerts.isEmpty() ? null : matchingAlerts.get(0).getTimestamp();

                    return new TrafficReadingWithAlertDTO(
                            r.getId(),
                            r.getLocation(),
                            r.getTimestamp(),
                            r.getTrafficDensity(),
                            r.getAvgSpeed(),
                            r.getCongestionLevel(),
                            alertTime,
                            matchingAlerts // ✅ pass all alerts with messages
                    );
                })

                .toList();

        // ✅ Only sort if sortBy is not blank
        if (sortBy != null && !sortBy.isBlank()) {
            Comparator<TrafficReadingWithAlertDTO> comparator = switch (sortBy) {
                case "trafficDensity" -> Comparator.comparing(TrafficReadingWithAlertDTO::getTrafficDensity);
                case "avgSpeed" -> Comparator.comparing(TrafficReadingWithAlertDTO::getAvgSpeed);
                case "alertTimestamp" -> Comparator.comparing(dto -> Optional.ofNullable(dto.getAlertTimestamp()).orElse(LocalDateTime.MIN));
                case "timestamp" -> Comparator.comparing(TrafficReadingWithAlertDTO::getTimestamp);
                default -> null;
            };

            if (comparator != null && "desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }

            if (comparator != null) {
                return result.stream().sorted(comparator).toList();
            }
        }

        return result; // ✅ No sort applied if sortBy is null or blank
    }


    public List<String> getDistinctLocations() {
        return repository.findAll()
                .stream()
                .map(TrafficTypeData::getLocation)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

}

