package com.DXC.iotbackend.mapper;

import java.util.List;

public interface SensorMapper<T, DTO> {
    List<DTO> mapReadingsWithAlerts(
            List<T> readings, List<?> alerts,
            String location, String status,
            java.time.LocalDateTime start, java.time.LocalDateTime end,
            String sortBy, String sortDir
    );

    List<String> getDistinctLocations(List<T> data);
}