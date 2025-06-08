package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.SensorData;
import com.dxc.iotbackend.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service responsible for generating and saving random sensor data.
 */
@Service
public class SensorDataService {

    private final SensorDataRepository repository;
    private final Random random = new Random();

    public SensorDataService(SensorDataRepository repository) {
        this.repository = repository;
    }

    public SensorData generateAndSaveRandomSensorData(String type) {
        double randomValue;

        switch (type.toLowerCase()) {
            case "traffic" -> randomValue = random.nextInt(100); // 0–99
            case "air" -> randomValue = 10 + (50 - 10) * random.nextDouble(); // 10–50
            case "light" -> randomValue = 100 + (900 - 100) * random.nextDouble(); // 100–900
            default -> throw new IllegalArgumentException("Unsupported sensor type: " + type);
        }

        SensorData data = new SensorData(type, randomValue, LocalDateTime.now());
        return repository.save(data);
    }
}
