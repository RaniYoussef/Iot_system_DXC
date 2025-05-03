package com.dxc.iotbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "air_pollution_sensor_data")
public class AirPollutionData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Location is required")
    private String location;

    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "50.0", inclusive = true)
    private float co;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "300.0", inclusive = true)
    private float ozone;

    @DecimalMin(value = "0.0", inclusive = true)
    private float pm2_5;

    @DecimalMin(value = "0.0", inclusive = true)
    private float pm10;

    @DecimalMin(value = "0.0", inclusive = true)
    private float no2;

    @DecimalMin(value = "0.0", inclusive = true)
    private float so2;

    @Pattern(regexp = "Good|Moderate|Unhealthy|Very Unhealthy|Hazardous",
            message = "Pollution level must be one of: Good, Moderate, Unhealthy, Very Unhealthy, Hazardous")
    private String pollutionLevel;

    public AirPollutionData() {}

    public AirPollutionData(String location, LocalDateTime timestamp, float pm2_5, float pm10, float co, float no2, float so2, float ozone, String pollutionLevel) {
        this.location = location;
        this.timestamp = timestamp;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
        this.ozone = ozone;
        this.pollutionLevel = pollutionLevel;
    }
}
