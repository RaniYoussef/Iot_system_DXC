package com.DXC.iotbackend.service;



import com.DXC.iotbackend.mapper.SensorMapper;
import com.DXC.iotbackend.mapper.TrafficMapper;
import com.DXC.iotbackend.model.Alert;
import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.repository.AlertRepository;
import com.DXC.iotbackend.repository.TrafficSensorDataRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.util.List;

@Service
public class TrafficSensorDataService extends BaseSensorDataService<TrafficTypeData, TrafficReadingWithAlertDTO> {

    private final TrafficSensorDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;
    private final TrafficMapper mapper = new TrafficMapper();

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
    @Override
    protected String getStatusFieldName() {
        return "congestionLevel";
    }


    @Override
    protected JpaRepository<TrafficTypeData, UUID> getRepository() {
        return repository;
    }

    @Override
    protected List<Alert> checkAlerts(TrafficTypeData data) {
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
    protected SensorMapper<TrafficTypeData, TrafficReadingWithAlertDTO> getMapper() {
        return mapper;
    }
}





//public List<TrafficTypeData> getAllData() {
//        return repository.findAll();
//    }
//
//
//    public Page<TrafficTypeData> getAllPaged(Pageable pageable) {
//        return repository.findAll(pageable);
//    }
//
//    public Page<TrafficTypeData> getFilteredTrafficData(
//            String location,
//            String congestionLevel,
//            LocalDateTime start,
//            LocalDateTime end,
//            Pageable pageable
//    ) {
//        return repository.findAll((root, query, cb) -> {
//            Predicate predicate = cb.conjunction();
//
//            if (location != null && !location.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(root.get("location"), location));
//            }
//            if (congestionLevel != null && !congestionLevel.isBlank()) {
//                predicate = cb.and(predicate, cb.equal(root.get("congestionLevel"), congestionLevel));
//            }
//            if (start != null) {
//                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), start));
//            }
//            if (end != null) {
//                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), end));
//            }
//
//            return predicate;
//        }, pageable);
//    }


//    public List<TrafficReadingWithAlertDTO> getTrafficReadingsWithAlertInfo(
//            String location,
//            String congestionLevel,
//            LocalDateTime start,
//            LocalDateTime end,
//            String sortBy,
//            String sortDir
//    ) {
//
//        List<TrafficTypeData> readings = repository.findAll(
//                buildFilterSpec(location, congestionLevel, start, end)
//        );
//
//        List<Alert> alerts = alertRepository.findAll();
//
//        TrafficMapper mapper = new TrafficMapper();
//        return mapper.mapReadingsWithAlerts(readings, alerts, location, congestionLevel, start, end, sortBy, sortDir);
//    }