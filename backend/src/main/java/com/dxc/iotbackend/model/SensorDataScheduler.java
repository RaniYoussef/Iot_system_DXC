package com.DXC.iotbackend.model;



import com.DXC.iotbackend.model.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class SensorDataScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();
    private final String BASE_URL = "http://localhost:8080/api";

    @Scheduled(fixedRate = 10000) // every 30 seconds
    public void sendRandomSensorData() {
        sendTrafficData();
        sendAirPollutionData();
        sendLightData();
    }

    private void sendTrafficData() {
        TrafficTypeData data = new TrafficTypeData(
                "Sidi Gaber",
                LocalDateTime.now(),
                random.nextInt(100),                           // trafficDensity
                10 + random.nextFloat() * 80,                  // avgSpeed
                randomLevel(new String[]{"Low", "Medium", "High"})
        );

        post("/traffic-sensor", data);
    }

    private void sendAirPollutionData() {
        AirPollutionData data = new AirPollutionData(
                "Smouha",
                LocalDateTime.now(),
                randomFloat(0, 35), // pm2_5
                randomFloat(0, 45), // pm10
                randomFloat(0, 50), // co
                randomFloat(0, 40), // no2
                randomFloat(0, 25), // so2
                randomFloat(0, 300), // ozone
                randomLevel(new String[]{"Good", "Moderate", "Unhealthy", "Very Unhealthy", "Hazardous"})
        );

        post("/air-pollution-sensor", data);
    }

    private void sendLightData() {
        StreetLightData data = new StreetLightData(
                "Corniche",
                LocalDateTime.now(),
                random.nextInt(101),              // brightnessLevel
                randomFloat(0, 5000),             // powerConsumption
                randomLevel(new String[]{"ON", "OFF"})
        );

        post("/light-sensor", data);
    }

    private void post(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForObject(BASE_URL + path, request, String.class);
        } catch (Exception e) {
            System.out.println("Error calling " + path + ": " + e.getMessage());
        }
    }

    private float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private String randomLevel(String[] levels) {
        return levels[random.nextInt(levels.length)];
    }
}
