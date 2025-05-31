package com.DXC.iotbackend.util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractSensorReadingMapper<T, DTO> {

    public List<DTO> mapReadingsWithAlerts(
            List<T> readings,
            List<?> alerts,
            String location,
            String status,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {
        List<DTO> result = readings.stream()
                .filter(r -> location == null || location.equalsIgnoreCase(Optional.ofNullable(getLocation(r)).orElse("")))
                .filter(r -> status == null || status.equalsIgnoreCase(Optional.ofNullable(getStatus(r)).orElse("")))
                .filter(r -> start == null || Optional.ofNullable(getTimestamp(r)).map(ts -> !ts.isBefore(start)).orElse(false))
                .filter(r -> end == null || Optional.ofNullable(getTimestamp(r)).map(ts -> !ts.isAfter(end)).orElse(false))
                .map(r -> {
                    List<?> matchingAlerts = alerts.stream()
                            .filter(a -> getSensorType().equalsIgnoreCase(getAlertType(a)))
                            .filter(a -> getLocation(r).equalsIgnoreCase(getAlertLocation(a)))
                            .filter(a -> getTimestamp(r).equals(getAlertTimestamp(a)))
                            .toList();
                    return mapToDTO(r, matchingAlerts);
                })
                .collect(Collectors.toList());

        if (sortBy != null && !sortBy.isBlank()) {
            Comparator<DTO> comparator = getComparator(sortBy);
            if (comparator != null && "desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            if (comparator != null) {
                result = result.stream().sorted(comparator).toList();
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