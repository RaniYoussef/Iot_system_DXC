package com.dxc.iotbackend.service;



import com.dxc.iotbackend.mapper.SensorMapper;
import com.dxc.iotbackend.mapper.TrafficMapper;
import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.model.TrafficTypeData;
import com.dxc.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.dxc.iotbackend.repository.AlertRepository;
import com.dxc.iotbackend.repository.TrafficSensorDataRepository;
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