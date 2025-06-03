package com.DXC.iotbackend.service;

import com.DXC.iotbackend.mapper.StreetLightMapper;
import com.DXC.iotbackend.mapper.SensorMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.StreetLightSensorDataRepository;
import com.DXC.iotbackend.service.BaseSensorDataService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StreetLightSensorDataService extends BaseSensorDataService<StreetLightData, StreetLightReadingWithAlertDTO> {

    private final StreetLightSensorDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;
    private final StreetLightMapper mapper = new StreetLightMapper();

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

    @Override
    protected JpaRepository<StreetLightData, UUID> getRepository() {
        return repository;
    }

    @Override
    protected List<Alert> checkAlerts(StreetLightData data) {
        return alertSettingService.checkThresholdViolations(data);
    }

    @Override
    protected AlertRepository getAlertRepository() {
        return alertRepository;
    }

    @Override
    protected AlertEmailService getAlertEmailService() {
        return alertEmailService;
    }

    @Override
    protected SensorMapper<StreetLightData, StreetLightReadingWithAlertDTO> getMapper() {
        return mapper;
    }
}







//public List<StreetLightReadingWithAlertDTO> getStreetLightReadingsWithAlertInfo(
//            String location,
//            String status,
//            LocalDateTime start,
//            LocalDateTime end,
//            String sortBy,
//            String sortDir
//    ) {
//
//        List<StreetLightData> readings = repository.findAll(
//                buildFilterSpec(location, status, start, end)
//        );
//
//        List<Alert> alerts = alertRepository.findAll();
//
//
//        StreetLightMapper mapper = new StreetLightMapper();
//        return mapper.mapReadingsWithAlerts(readings, alerts, location, status, start, end, sortBy, sortDir);
//    }



//public List<StreetLightData> getAllData() {
//        return repository.findAll();
//    }
//
//    public Page<StreetLightData> getPagedData(Pageable pageable) {
//        return repository.findAll(pageable);
//    }
//
//    public Page<StreetLightData> getFilteredStreetLightData(
//            String location,
//            String status,
//            LocalDateTime start,
//            LocalDateTime end,
//            Pageable pageable
//    ) {
//        return repository.findAll((root, query, cb) -> {
//            Predicate predicate = cb.conjunction();
//
//            if (location != null && !location.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("location")), location.toLowerCase()));
//            }
//
//            if (status != null && !status.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
//            }
//
//            if (start != null) {
//                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), start));
//            }
//
//            if (end != null) {
//                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), end));
//            }
//
//            return predicate;
//        }, pageable);
//    }