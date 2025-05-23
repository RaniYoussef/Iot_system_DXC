package com.DXC.iotbackend.service;



import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.repository.TrafficSensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrafficSensorDataService {

    private final TrafficSensorDataRepository repository;

    public TrafficSensorDataService(TrafficSensorDataRepository repository) {
        this.repository = repository;
    }

    public TrafficTypeData saveData(TrafficTypeData data) {
        return repository.save(data);
    }

    public List<TrafficTypeData> getAllData() {
        return repository.findAll();
    }
}

