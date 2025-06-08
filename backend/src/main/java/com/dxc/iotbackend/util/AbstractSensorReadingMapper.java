package com.dxc.iotbackend.util;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractSensorReadingMapper<E, D> {

    public List<D> mapReadingsWithAlerts(
            List<E> readings,
            List<?> alerts,
            String locationFilter,
            String statusFilter,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {
        List<D> result = readings.stream()
                .filter(r -> matchesLocation(r, locationFilter))
                .filter(r -> matchesStatus(r, statusFilter))
                .filter(r -> withinStartTime(r, start))
                .filter(r -> withinEndTime(r, end))
                .map(r -> {
                    List<?> matchingAlerts = alerts.stream()
                            .filter(a -> sensorTypeMatches(a))
                            .filter(a -> locationsMatch(r, a))
                            .filter(a -> timestampsMatch(r, a))
                            .toList();
                    return mapToDTO(r, matchingAlerts);
                })
                .toList();

        if (sortBy != null && !sortBy.isBlank()) {
            Comparator<D> comparator = getComparator(sortBy);
            if (comparator != null && "desc".equalsIgnoreCase(sortDir)) {
                comparator = comparator.reversed();
            }
            if (comparator != null) {
                result = result.stream().sorted(comparator).toList();
            }
        }

        return result;
    }

    public List<String> getDistinctLocations(List<E> readings) {
        return readings.stream()
                .map(this::getLocation)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private boolean matchesLocation(E reading, String filter) {
        return filter == null || filter.isBlank()
                || filter.equalsIgnoreCase(Optional.ofNullable(getLocation(reading)).orElse(""));
    }

    private boolean matchesStatus(E reading, String filter) {
        return filter == null || filter.isBlank()
                || filter.equalsIgnoreCase(Optional.ofNullable(getStatus(reading)).orElse(""));
    }

    private boolean withinStartTime(E reading, LocalDateTime start) {
        return start == null || Optional.ofNullable(getTimestamp(reading))
                .map(ts -> !ts.isBefore(start)).orElse(false);
    }

    private boolean withinEndTime(E reading, LocalDateTime end) {
        return end == null || Optional.ofNullable(getTimestamp(reading))
                .map(ts -> !ts.isAfter(end)).orElse(false);
    }

    private boolean sensorTypeMatches(Object alert) {
        return getSensorType().equalsIgnoreCase(getAlertType(alert));
    }

    private boolean locationsMatch(E reading, Object alert) {
        return getLocation(reading).equalsIgnoreCase(getAlertLocation(alert));
    }

    private boolean timestampsMatch(E reading, Object alert) {
        return getTimestamp(reading).equals(getAlertTimestamp(alert));
    }

    protected abstract String getSensorType();

    protected abstract String getLocation(E reading);

    protected abstract String getStatus(E reading);

    protected abstract LocalDateTime getTimestamp(E reading);

    protected abstract String getAlertType(Object alert);

    protected abstract String getAlertLocation(Object alert);

    protected abstract LocalDateTime getAlertTimestamp(Object alert);

    protected abstract D mapToDTO(E reading, List<?> matchingAlerts);

    protected abstract Comparator<D> getComparator(String sortBy);
}
