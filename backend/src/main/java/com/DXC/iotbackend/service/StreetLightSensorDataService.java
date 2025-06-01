package com.DXC.iotbackend.service;

import com.DXC.iotbackend.mapper.StreetLightMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.StreetLightSensorDataRepository;
import com.DXC.iotbackend.util.SensorFilterSpecificationBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class StreetLightSensorDataService {

    private final StreetLightMapper mapper = new StreetLightMapper();

    private final StreetLightSensorDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;

    public StreetLightSensorDataService(
            StreetLightSensorDataRepository repository,
            AlertSettingService alertSettingService,
            AlertRepository alertRepository,
            AlertEmailService alertEmailService
    ) {
        this.repository = repository;
        this.alertSettingService = alertSettingService;
        this.alertRepository = alertRepository;
        this.alertEmailService = alertEmailService;
    }

    public StreetLightData saveData(StreetLightData data) {
        StreetLightData saved = repository.save(data);

        List<Alert> alerts = alertSettingService.checkThresholdViolations(data);
        for (Alert alert : alerts) {
            alertRepository.save(alert);
            alertEmailService.sendAlertEmail(alert);
        }
        return saved;
    }

    public List<StreetLightData> getAllData() {
        return repository.findAll();
    }

    public Page<StreetLightData> getPagedData(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<StreetLightData> getFilteredStreetLightData(
            String location,
            String status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        return repository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (location != null && !location.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("location")), location.toLowerCase()));
            }

            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
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

    private Specification<StreetLightData> buildFilterSpec(
            String location,
            String status,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return SensorFilterSpecificationBuilder.buildCommonFilters(
                location, status, start, end, "location", "status"
        );
    }



    public List<StreetLightReadingWithAlertDTO> getStreetLightReadingsWithAlertInfo(
            String location,
            String status,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {

        List<StreetLightData> readings = repository.findAll(
                buildFilterSpec(location, status, start, end)
        );

        List<Alert> alerts = alertRepository.findAll();


        StreetLightMapper mapper = new StreetLightMapper();
        return mapper.mapReadingsWithAlerts(readings, alerts, location, status, start, end, sortBy, sortDir);
    }

    public List<String> getDistinctLocations() {
        return mapper.getDistinctLocations(repository.findAll());
    }






    public Page<StreetLightReadingWithAlertDTO> getSStreetLightReadingsWithAlertInfo(
            String location,
            String status,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) {
        Specification<StreetLightData> spec = buildFilterSpec(location, status, start, end);

        Page<StreetLightData> page = repository.findAll(spec, pageable);
        List<Alert> alerts = alertRepository.findAll();

        List<StreetLightReadingWithAlertDTO> dtoList = mapper.mapReadingsWithAlerts(
                page.getContent(), alerts, location, status, start, end, sortBy, sortDir
        );

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }
}
