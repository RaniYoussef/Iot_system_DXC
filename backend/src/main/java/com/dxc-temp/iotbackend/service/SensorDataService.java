package com.dxc.iotbackend.service;
import com.dxc.iotbackend.model.SensorData;
import com.dxc.iotbackend.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;


//the main logic of generating random sensor data and saving it to the database.
@Service
public class SensorDataService {

    private final SensorDataRepository repository; //Injects the SensorDataRepository (so I can save to DB)
    private final Random random = new Random();

    public SensorDataService(SensorDataRepository repository) {
        this.repository = repository;
    }

    public SensorData generateAndSaveRandomSensorData(String type) {
        double randomValue = 0.0;

        switch (type.toLowerCase()) {
            case "traffic" -> randomValue = random.nextInt(100); // 0–99
            case "air" -> randomValue = 10 + (50 - 10) * random.nextDouble(); // 10–50
            case "light" -> randomValue = 100 + (900 - 100) * random.nextDouble(); // 100–900
            default -> throw new IllegalArgumentException("Unsupported sensor type");
        }

        SensorData data = new SensorData(type, randomValue, LocalDateTime.now());
        return repository.save(data); //Saves it to the DB
    }
}

//This method takes a string like "traffic" or "air" and Generates a random value depending on type. Creates a new SensorData object, Saves it in the database.