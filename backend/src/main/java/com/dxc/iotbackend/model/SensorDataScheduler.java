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
    public void logInitialization() {
        LOGGER.info("SensorDataScheduler initialized with baseUrl: {}", baseUrl);
    }

    @Scheduled(fixedRate = 60000)
    public void sendSensorData() {
        postTrafficData();
        postAirData();
        postLightData();
    }

    private void postTrafficData() {
        TrafficTypeData data = new TrafficTypeData(
                getRandomLocation(),
                LocalDateTime.now(),
                random.nextInt(500),
                10 + random.nextFloat() * 80,
                getRandomFrom("Low", "Moderate", "High", "Severe")
        );
        postJson(trafficPath, data);
    }

    private void postAirData() {
        AirPollutionData data = new AirPollutionData(
                getRandomLocation(),
                LocalDateTime.now(),
                rand(0, 35), rand(0, 45), rand(0, 50), rand(0, 40), rand(0, 25), rand(0, 300),
                getRandomFrom("Good", "Moderate", "Unhealthy", "Very Unhealthy", "Hazardous")
        );
        postJson(airPath, data);
    }

    private void postLightData() {
        StreetLightData data = new StreetLightData(
                getRandomLocation(),
                LocalDateTime.now(),
                random.nextInt(101),
                rand(0, 5000),
                getRandomFrom("ON", "OFF")
        );
        postJson(lightPath, data);
    }

    private void postJson(String path, Object payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForObject(baseUrl + path, entity, String.class);
            LOGGER.info("Successfully posted to {}", path);
        } catch (Exception e) {
            LOGGER.error("Error posting to {}: {}", path, e.getMessage());
        }
    }

    private float rand(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private String getRandomLocation() {
        return LOCATIONS[random.nextInt(LOCATIONS.length)];
    }

    private String getRandomFrom(String... options) {
        return options[random.nextInt(options.length)];
    }
}