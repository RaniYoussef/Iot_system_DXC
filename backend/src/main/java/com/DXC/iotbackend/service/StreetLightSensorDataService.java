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

