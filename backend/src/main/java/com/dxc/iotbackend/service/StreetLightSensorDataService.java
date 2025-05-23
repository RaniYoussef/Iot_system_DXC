package com.DXC.iotbackend.service;

import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.repository.StreetLightSensorDataRepository;
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
