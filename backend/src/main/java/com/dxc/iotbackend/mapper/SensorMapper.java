package com.dxc.iotbackend.mapper;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorMapper<Entity, Dto> {

    List<Dto> mapReadingsWithAlerts(
        List<Entity> readings,
        List<?> alerts,
        String location,
        String status,
        LocalDateTime start,
        LocalDateTime end,
        String sortBy,
        String sortDir
    );

    List<String> getDistinctLocations(List<Entity> data);
}
