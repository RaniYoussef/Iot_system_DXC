package com.DXC.iotbackend.service;

import com.DXC.iotbackend.mapper.SensorMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.service.AlertEmailService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class BaseSensorDataService<T, DTO> {

    protected abstract JpaRepository<T, UUID> getRepository();
    protected abstract List<Alert> checkAlerts(T data);
    protected abstract AlertRepository getAlertRepository();
    protected abstract AlertEmailService getAlertEmailService();
    protected abstract SensorMapper<T, DTO> getMapper();

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

    public Page<DTO> getReadingsWithAlertInfo(
            String filter1,
            String filter2,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) {
        // Step 1: Build filtering specification
        Specification<T> spec = com.DXC.iotbackend.util.SensorFilterSpecificationBuilder
                .buildCommonFilters(filter1, filter2, start, end, "location", "status");

        // Step 2: Get all filtered entities (not just one page)
        List<T> filteredEntities = specRepo().findAll(spec);

        // Step 3: Fetch all alerts once
        List<Alert> alerts = getAlertRepository().findAll();

        // Step 4: Map with alerts, filter, sort entire list
        List<DTO> fullDtoList = getMapper().mapReadingsWithAlerts(
                filteredEntities, alerts, filter1, filter2, start, end, sortBy, sortDir
        );

        // Step 5: Perform manual pagination (after sort is applied)
        int startIdx = Math.min((int) pageable.getOffset(), fullDtoList.size());
        int endIdx = Math.min(startIdx + pageable.getPageSize(), fullDtoList.size());
        List<DTO> paginatedList = fullDtoList.subList(startIdx, endIdx);

        // Step 6: Return manually paged DTO list
        return new PageImpl<>(paginatedList, pageable, fullDtoList.size());
    }

    public List<String> getDistinctLocations() {
        return getMapper().getDistinctLocations(getRepository().findAll());
    }
}