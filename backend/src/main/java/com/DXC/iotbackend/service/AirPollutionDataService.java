package com.DXC.iotbackend.service;

import com.DXC.iotbackend.model.AirPollutionData;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.repository.AirPollutionDataRepository;
import com.DXC.iotbackend.repository.AlertRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirPollutionDataService {

    private final AirPollutionDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;

    public AirPollutionDataService(
            AirPollutionDataRepository repository,
            AlertSettingService alertSettingService,
            AlertRepository alertRepository,
            AlertEmailService alertEmailService
    ) {
        this.repository = repository;
        this.alertSettingService = alertSettingService;
        this.alertRepository = alertRepository;
        this.alertEmailService = alertEmailService;
    }

    public AirPollutionData saveData(AirPollutionData data) {
        AirPollutionData saved = repository.save(data);

        List<Alert> alerts = alertSettingService.checkThresholdViolations(data);
        for (Alert alert : alerts) {
            alertRepository.save(alert);
            alertEmailService.sendAlertEmail(alert);
        }

        return saved;
    }

    public List<AirPollutionData> getAllData() {
        return repository.findAll();
    }



    public Page<AirPollutionData> getPagedData(Pageable pageable) {
        return repository.findAll(pageable);
    }


    public Page<AirPollutionData> getFilteredAirPollutionData(
            String location,
            String pollutionLevel,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        return repository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (location != null && !location.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("location"), location));
            }
            if (pollutionLevel != null && !pollutionLevel.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("pollutionLevel"), pollutionLevel));
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
