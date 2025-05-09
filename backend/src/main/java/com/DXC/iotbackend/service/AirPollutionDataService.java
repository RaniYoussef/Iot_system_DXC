package com.DXC.iotbackend.service;

import com.DXC.iotbackend.model.AirPollutionData;
import com.DXC.iotbackend.repository.AirPollutionDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirPollutionDataService {

    private final AirPollutionDataRepository repository;

    public AirPollutionDataService(AirPollutionDataRepository repository) {
        this.repository = repository;
    }

    public AirPollutionData saveData(AirPollutionData data) {
        return repository.save(data);
    }

    public List<AirPollutionData> getAllData() {
        return repository.findAll();
    }
}
