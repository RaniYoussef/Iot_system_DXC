package com.dxc.iotbackend.service;

import com.dxc.iotbackend.mapper.SensorMapper;
import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.repository.AlertRepository;
import com.dxc.iotbackend.service.AlertEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.dxc.iotbackend.util.SensorFilterSpecificationBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class BaseSensorDataService<T, DTO> {

    protected abstract JpaRepository<T, UUID> getRepository();
    protected abstract List<Alert> checkAlerts(T data);
    protected abstract AlertRepository getAlertRepository();
    protected abstract AlertEmailService getAlertEmailService();
    protected abstract SensorMapper<T, DTO> getMapper();

    protected abstract String getStatusFieldName();

    @SuppressWarnings("unchecked")
    private JpaSpecificationExecutor<T> specRepo() {
        return (JpaSpecificationExecutor<T>) getRepository();
    }

    public T saveData(T data) {
        T saved = getRepository().save(data);
        List<Alert> alerts = checkAlerts(data);
        for (Alert alert : alerts) {
            getAlertRepository().save(alert);
            getAlertEmailService().sendAlertEmail(alert);
        }
        return saved;
    }

    protected Specification<T> buildFilterSpec(String location, String statusOrLevel,
                                               LocalDateTime start, LocalDateTime end) {
        return SensorFilterSpecificationBuilder.buildCommonFilters(
                location,
                statusOrLevel,
                start,
                end,
                "location",
                getStatusFieldName()
        );
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseSensorDataService.class);

    public Page<DTO> getReadingsWithAlertInfo(
            String filter1,
            String filter2,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) {
        logger.info("Starting getReadingsWithAlertInfo with filters: filter1={}, filter2={}, start={}, end={}, sortBy={}, sortDir={}",
                filter1, filter2, start, end, sortBy, sortDir);

        // Step 1: Build filtering specification
        Specification<T> spec = buildFilterSpec(filter1, filter2, start, end);
        logger.info("Built filter specification: {}", spec);

        // Step 2: Get all filtered entities
        List<T> filteredEntities = specRepo().findAll(spec);
        logger.info("Fetched {} filtered entities", filteredEntities.size());
        logger.info("Filtered Entities: {}", filteredEntities);

        // Step 3: Fetch all alerts once
        List<Alert> alerts = getAlertRepository().findAll();

        // Step 4: Map with alerts
        List<DTO> fullDtoList = getMapper().mapReadingsWithAlerts(
                filteredEntities, alerts, filter1, filter2, start, end, sortBy, sortDir
        );

        // Step 5: Manual pagination
        int startIdx = Math.min((int) pageable.getOffset(), fullDtoList.size());
        int endIdx = Math.min(startIdx + pageable.getPageSize(), fullDtoList.size());
        List<DTO> paginatedList = fullDtoList.subList(startIdx, endIdx);
//        logger.info("Paginated list from index {} to {} (page size: {})", startIdx, endIdx, pageable.getPageSize());

        return new PageImpl<>(paginatedList, pageable, fullDtoList.size());
    }

    public List<String> getDistinctLocations() {
        return getMapper().getDistinctLocations(getRepository().findAll());
    }
}