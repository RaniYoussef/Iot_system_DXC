package com.DXC.iotbackend.service;

import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.StreetLightSensorDataRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StreetLightSensorDataService {

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

}