package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.AirPollutionData;
import com.dxc.iotbackend.repository.AirPollutionDataRepository;
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
