package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.repository.StreetLightSensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreetLightSensorDataService {

    private final StreetLightSensorDataRepository repository;

    public StreetLightSensorDataService(StreetLightSensorDataRepository repository) {
        this.repository = repository;
    }

    public StreetLightData saveData(StreetLightData data) {
        return repository.save(data);
    }

    public List<StreetLightData> getAllData() {
        return repository.findAll();
    }
}
