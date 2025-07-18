package com.dxc.iotbackend.service;

import com.dxc.iotbackend.mapper.AirPollutionMapper;
import com.dxc.iotbackend.mapper.SensorMapper;
import com.dxc.iotbackend.model.AirPollutionData;
import com.dxc.iotbackend.model.Alert;
import com.dxc.iotbackend.payload.AirPollutionReadingWithAlertDTO;
import com.dxc.iotbackend.repository.AirPollutionDataRepository;
import com.dxc.iotbackend.repository.AlertRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AirPollutionDataService extends BaseSensorDataService<AirPollutionData, AirPollutionReadingWithAlertDTO> {

    private final AirPollutionDataRepository repository;
    private final AlertSettingService alertSettingService;
    private final AlertRepository alertRepository;
    private final AlertEmailService alertEmailService;
    private final AirPollutionMapper mapper = new AirPollutionMapper();

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

    @Override
    protected JpaRepository<AirPollutionData, UUID> getRepository() {
        return repository;
    }

    @Override
    protected List<Alert> checkAlerts(AirPollutionData data) {
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
    protected SensorMapper<AirPollutionData, AirPollutionReadingWithAlertDTO> getMapper() {
        return mapper;
    }

    @Override
    protected String getStatusFieldName() {
        return "pollutionLevel";
    }
}