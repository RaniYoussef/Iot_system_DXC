package com.DXC.iotbackend.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.DXC.iotbackend.mapper.SensorMapper;
import com.DXC.iotbackend.mapper.TrafficMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.TrafficSensorDataRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;

@Service
//public class TrafficSensorDataService extends BaseSensorDataService<TrafficTypeData, TrafficReadingWithAlertDTO> {
public class TrafficSensorDataService {

    private static final Logger logger = LoggerFactory.getLogger(TrafficSensorDataService.class);

    private final TrafficSensorDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;
    //private final TrafficMapper mapper = new TrafficMapper();

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

    public Page<TrafficReadingWithAlertDTO> getTrafficReadingsWithAlertInfo(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) {
        logger.info("Fetching traffic readings with alerts | location={}, congestionLevel={}, start={}, end={}, sortBy={}, sortDir={}",
                location, congestionLevel, start, end, sortBy, sortDir);

        List<TrafficTypeData> readings = repository.findAll();
        logger.info("Fetched {} raw traffic readings", readings.size());

        List<Alert> alerts = alertRepository.findAll();
        logger.info("Fetched {} total alerts from database", alerts.size());

        List<TrafficReadingWithAlertDTO> result = readings.stream()
                .filter(r -> location == null || location.isBlank() ||
                        location.equalsIgnoreCase(Optional.ofNullable(r.getLocation()).orElse("")))
                .filter(r -> congestionLevel == null || congestionLevel.isBlank() ||
                        congestionLevel.equalsIgnoreCase(Optional.ofNullable(r.getCongestionLevel()).orElse("")))
                .filter(r -> start == null || Optional.ofNullable(r.getTimestamp()).map(ts -> !ts.isBefore(start)).orElse(false))
                .filter(r -> end == null || Optional.ofNullable(r.getTimestamp()).map(ts -> !ts.isAfter(end)).orElse(false))
                .map(r -> {
                    List<Alert> matchingAlerts = alerts.stream()
                            .filter(a -> a.getType().equalsIgnoreCase("Traffic"))
                            .filter(a -> a.getLocation().equalsIgnoreCase(r.getLocation()))
                            .filter(a -> a.getTimestamp().equals(r.getTimestamp()))
                            .toList();

                    logger.info("Reading ID={} matched {} alert(s)", r.getId(), matchingAlerts.size());
                    matchingAlerts.forEach(alert -> logger.info(" -> Matched Alert: {}", alert));

                    LocalDateTime alertTime = matchingAlerts.isEmpty() ? null : matchingAlerts.get(0).getTimestamp();

                    TrafficReadingWithAlertDTO dto = new TrafficReadingWithAlertDTO(
                            r.getId(),
                            r.getLocation(),
                            r.getTimestamp(),
                            r.getTrafficDensity(),
                            r.getAvgSpeed(),
                            r.getCongestionLevel(),
                            alertTime,
                            matchingAlerts
                    );

                    logger.info("Mapped DTO: {}", dto);
                    return dto;
                })
                .toList();

        logger.info("Total DTOs after filtering and mapping: {}", result.size());

        // Sort if required
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
                result = result.stream().sorted(comparator).toList();
                logger.info("Sorted DTOs by '{}' in '{}' order", sortBy, sortDir);
            }
        }

        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), result.size());
        List<TrafficReadingWithAlertDTO> pageContent = result.subList(startIdx, endIdx);

        logger.info("Returning paginated DTOs from {} to {} of total {}", startIdx, endIdx, result.size());
        return new PageImpl<>(pageContent, pageable, result.size());
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


//    @Override
//    protected String getStatusFieldName() {
//        return "congestionLevel";
//    }
//
//
//    @Override
//    protected JpaRepository<TrafficTypeData, UUID> getRepository() {
//        return repository;
//    }
//
//    @Override
//    protected List<Alert> checkAlerts(TrafficTypeData data) {
//        return alertSettingService.checkThresholdViolations(data);
//    }
//
//    @Override
//    protected AlertRepository getAlertRepository() {
//        return alertRepository;
//    }
//
//    @Override
//    protected AlertEmailService getAlertEmailService() {
//        return alertEmailService;
//    }
//
//    @Override
//    protected SensorMapper<TrafficTypeData, TrafficReadingWithAlertDTO> getMapper() {
//        return mapper;
//    }
//}





//public List<TrafficTypeData> getAllData() {
//        return repository.findAll();
//    }
//
//
//    public Page<TrafficTypeData> getAllPaged(Pageable pageable) {
//        return repository.findAll(pageable);
//    }
//
//    public Page<TrafficTypeData> getFilteredTrafficData(
//            String location,
//            String congestionLevel,
//            LocalDateTime start,
//            LocalDateTime end,
//            Pageable pageable
//    ) {
//        return repository.findAll((root, query, cb) -> {
//            Predicate predicate = cb.conjunction();
//
//            if (location != null && !location.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(root.get("location"), location));
//            }
//            if (congestionLevel != null && !congestionLevel.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(root.get("congestionLevel"), congestionLevel));
//            }
//            if (start != null) {
//                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), start));
//            }
//            if (end != null) {
//                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), end));
//            }
//
//            return predicate;
//        }, pageable);
//    }


//    public List<TrafficReadingWithAlertDTO> getTrafficReadingsWithAlertInfo(
//            String location,
//            String congestionLevel,
//            LocalDateTime start,
//            LocalDateTime end,
//            String sortBy,
//            String sortDir
//    ) {
//
//        List<TrafficTypeData> readings = repository.findAll(
//                buildFilterSpec(location, congestionLevel, start, end)
//        );
//
//        List<Alert> alerts = alertRepository.findAll();
//
//        TrafficMapper mapper = new TrafficMapper();
//        return mapper.mapReadingsWithAlerts(readings, alerts, location, congestionLevel, start, end, sortBy, sortDir);
//    }