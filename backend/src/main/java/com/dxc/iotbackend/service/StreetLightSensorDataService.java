package com.dxc.iotbackend.service;

import com.dxc.iotbackend.mapper.StreetLightMapper;
import com.dxc.iotbackend.mapper.SensorMapper;
import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.dxc.iotbackend.repository.AlertRepository;
import com.dxc.iotbackend.repository.StreetLightSensorDataRepository;
import com.dxc.iotbackend.service.BaseSensorDataService;
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
    protected String getStatusFieldName() {
        return "status";
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