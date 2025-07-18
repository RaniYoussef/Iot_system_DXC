package com.dxc.iotbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractSensorReadingMapper<T, DTO> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSensorReadingMapper.class);

    public List<DTO> mapReadingsWithAlerts(
            List<T> readings,
            List<?> alerts,
            String filter1,
            String filter2,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {
//        logger.info("Initial Readings: {}", readings);
//        logger.info("Initial Alerts: {}", alerts);

        List<DTO> result = readings.stream()
                .filter(r -> {
                    boolean passed = filter1 == null || filter1.trim().isEmpty() ||
                            filter1.equalsIgnoreCase(Optional.ofNullable(getLocation(r)).orElse(""));
//                    if (!passed) logger.info("Filtered out by filter1: {}", r);
                    return passed;
                })
                .filter(r -> {
                    boolean passed = filter2 == null || filter2.trim().isEmpty() ||
                            filter2.equalsIgnoreCase(Optional.ofNullable(getStatus(r)).orElse(""));
                    if (!passed) logger.info("Filtered out by filter2: {}", r);
                    return passed;
                })
                .filter(r -> {
                    boolean passed = start == null ||
                            Optional.ofNullable(getTimestamp(r)).map(ts -> !ts.isBefore(start)).orElse(false);
//                    if (!passed) logger.info("Filtered out by start date: {}", r);
                    return passed;
                })
                .filter(r -> {
                    boolean passed = end == null ||
                            Optional.ofNullable(getTimestamp(r)).map(ts -> !ts.isAfter(end)).orElse(false);
//                    if (!passed) logger.info("Filtered out by end date: {}", r);
                    return passed;
                })
                .map(r -> {
                    List<?> matchingAlerts = alerts.stream()
                            .filter(a -> getSensorType().equalsIgnoreCase(getAlertType(a)))
                            .filter(a -> getLocation(r).equalsIgnoreCase(getAlertLocation(a)))
                            .filter(a -> getTimestamp(r).equals(getAlertTimestamp(a)))
                            .toList();

//                    logger.info("Matching alerts for reading {}: {}", r, matchingAlerts);

                    DTO dto = mapToDTO(r, matchingAlerts);
//                    logger.info("Mapped DTO: {}", dto);
                    return dto;
                })
                .collect(Collectors.toList());

//        logger.info("Total DTOs after filtering and mapping: {}", result.size());

        if (sortBy != null && !sortBy.isBlank()) {
            Comparator<DTO> comparator = getComparator(sortBy);
            if (comparator != null && "desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            if (comparator != null) {
                result = result.stream().sorted(comparator).toList();
//                logger.info("Sorted DTOs by '{}' in '{}' order", sortBy, sortDir);
            }
        }

        return result;
    }

    // === Shared reusable method: get distinct locations ===
    public List<String> getDistinctLocations(List<T> readings) {
        return readings.stream()
                .map(this::getLocation)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    // Abstract methods to be implemented in each sensor's subclass
    protected abstract String getSensorType(); // e.g., "Traffic", "Street_Light"
    protected abstract String getLocation(T reading);
    protected abstract String getStatus(T reading);
    protected abstract LocalDateTime getTimestamp(T reading);

    protected abstract String getAlertType(Object alert);
    protected abstract String getAlertLocation(Object alert);
    protected abstract LocalDateTime getAlertTimestamp(Object alert);

    protected abstract DTO mapToDTO(T reading, List<?> matchingAlerts);
    protected abstract Comparator<DTO> getComparator(String sortBy);
}