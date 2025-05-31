package com.DXC.iotbackend.service;



import com.DXC.iotbackend.mapper.TrafficMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.TrafficSensorDataRepository;
import com.DXC.iotbackend.util.SensorFilterSpecificationBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;

@Service
public class TrafficSensorDataService {

    private final TrafficMapper mapper = new TrafficMapper();

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


    private Specification<TrafficTypeData> buildFilterSpec(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return SensorFilterSpecificationBuilder.buildCommonFilters(
                location, congestionLevel, start, end, "location", "congestionLevel"
        );
    }


    public List<TrafficReadingWithAlertDTO> getTrafficReadingsWithAlertInfo(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir
    ) {

        List<TrafficTypeData> readings = repository.findAll(
                buildFilterSpec(location, congestionLevel, start, end)
        );

        List<Alert> alerts = alertRepository.findAll();

        TrafficMapper mapper = new TrafficMapper();
        return mapper.mapReadingsWithAlerts(readings, alerts, location, congestionLevel, start, end, sortBy, sortDir);
    }

    public List<String> getDistinctLocations() {
        return mapper.getDistinctLocations(repository.findAll());
    }






    public Page<TrafficReadingWithAlertDTO> getTTrafficReadingsWithAlertInfo(
            String location,
            String congestionLevel,
            LocalDateTime start,
            LocalDateTime end,
            String sortBy,
            String sortDir,
            Pageable pageable
    ) {
        Specification<TrafficTypeData> spec = buildFilterSpec(location, congestionLevel, start, end);

        Page<TrafficTypeData> page = repository.findAll(spec, pageable);
        List<Alert> alerts = alertRepository.findAll();

        List<TrafficReadingWithAlertDTO> dtoList = mapper.mapReadingsWithAlerts(
                page.getContent(), alerts, location, congestionLevel, start, end, sortBy, sortDir
        );

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }
}

