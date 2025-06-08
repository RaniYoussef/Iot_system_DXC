package com.dxc.iotbackend.scheduler;

import com.dxc.iotbackend.model.AirPollutionData;
import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.model.TrafficTypeData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataScheduler.class);
    private static final String[] LOCATIONS = {"Sidi Gaber", "Smouha", "Stanley", "Miami", "Sporting", "Mandara"};

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${scheduler.BASE_URL}")
    private String baseUrl;

    @Value("${sensor.traffic-path}")
    private String trafficPath;

    @Value("${sensor.air-path}")
    private String airPath;

    @Value("${sensor.light-path}")
    private String lightPath;

    @PostConstruct
    public void init() {
        LOGGER.info("SensorDataScheduler initialized with baseUrl: {}", baseUrl);
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void sendRandomSensorData() {
        sendTrafficData();
        sendAirPollutionData();
        sendLightData();
    }

    private void sendTrafficData() {
        String location = getRandomLocation();
        TrafficTypeData data = new TrafficTypeData(
                location,
                LocalDateTime.now(),
                random.nextInt(500),                           // trafficDensity
                10 + random.nextFloat() * 80,                  // avgSpeed
                getRandomValue(new String[]{"Low", "Moderate", "High", "Severe"})
        );
        postToEndpoint(trafficPath, data);
    }

    private void sendAirPollutionData() {
        String location = getRandomLocation();
        AirPollutionData data = new AirPollutionData(
                location,
                LocalDateTime.now(),
                randomFloat(0, 35),
                randomFloat(0, 45),
                randomFloat(0, 50),
                randomFloat(0, 40),
                randomFloat(0, 25),
                randomFloat(0, 300),
                getRandomValue(new String[]{"Good", "Moderate", "Unhealthy", "Very Unhealthy", "Hazardous"})
        );
        postToEndpoint(airPath, data);
    }

    private void sendLightData() {
        String location = getRandomLocation();
        StreetLightData data = new StreetLightData(
                location,
                LocalDateTime.now(),
                random.nextInt(101),
                randomFloat(0, 5000),
                getRandomValue(new String[]{"ON", "OFF"})
        );
        postToEndpoint(lightPath, data);
    }

    private void postToEndpoint(String path, Object payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForObject(baseUrl + path, request, String.class);
            LOGGER.info("Data posted to {} successfully", path);
        } catch (Exception ex) {
            LOGGER.error("Failed to post data to {}: {}", path, ex.getMessage());
        }
    }

    private float randomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private String getRandomLocation() {
        return LOCATIONS[random.nextInt(LOCATIONS.length)];
    }

    private String getRandomValue(String[] values) {
        return values[random.nextInt(values.length)];
    }
}