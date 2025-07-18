package com.DXC.iotbackend.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class SensorDataScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Use SecureRandom for better randomness quality and security
    private final Random random = new SecureRandom();

    @Value("${scheduler.BASE_URL}")
    private String BASE_URL;

    @Value("${sensor.traffic-path}")
    private String trafficPath;

    @Value("${sensor.air-path}")
    private String airPath;

    @Value("${sensor.light-path}")
    private String lightPath;

    private static final String[] LOCATIONS = {"Sidi Gaber", "Smouha", "Stanley", "Miami", "Sporting", "Mandara"};

    @Scheduled(fixedRate = 60000) // every 1 min
    public void sendRandomSensorData() {
        sendTrafficData();
        sendAirPollutionData();
        sendLightData();
    }

    private void sendTrafficData() {
        String randomLocation = randomLocation();
        TrafficTypeData data = new TrafficTypeData(
                randomLocation,
                LocalDateTime.now(),
                random.nextInt(500),                         // trafficDensity
                10 + random.nextFloat() * 80,                // avgSpeed
                randomLevel(new String[]{"Low", "Moderate", "High", "Severe"})
        );
        post(trafficPath, data);
    }

    private void sendAirPollutionData() {
        String randomLocation = randomLocation();
        AirPollutionData data = new AirPollutionData(
                randomLocation,
                LocalDateTime.now(),
                randomFloat(0, 35),      // pm2_5
                randomFloat(0, 45),      // pm10
                randomFloat(0, 50),      // co
                randomFloat(0, 40),      // no2
                randomFloat(0, 25),      // so2
                randomFloat(0, 300),     // ozone
                randomLevel(new String[]{"Good", "Moderate", "Unhealthy", "Very Unhealthy", "Hazardous"})
        );
        post(airPath, data);
    }

    private void sendLightData() {
        String randomLocation = randomLocation();
        StreetLightData data = new StreetLightData(
                randomLocation,
                LocalDateTime.now(),
                random.nextInt(101),              // brightnessLevel
                randomFloat(0, 5000),             // powerConsumption
                randomLevel(new String[]{"ON", "OFF"})
        );
        post(lightPath, data);
    }

    private void post(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForObject(BASE_URL + path, request, String.class);
        } catch (Exception e) {
            System.err.println("Error calling " + path + ": " + e.getMessage());
        }
    }

    private float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private String randomLevel(String[] levels) {
        return levels[random.nextInt(levels.length)];
    }

    private String randomLocation() {
        return LOCATIONS[random.nextInt(LOCATIONS.length)];
    }
}
