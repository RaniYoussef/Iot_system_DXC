package com.DXC.iotbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "traffic_sensor_data")
public class TrafficTypeData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Location must not be empty")
    private String location;

    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;

    @Min(value = 0, message = "Traffic density must be at least 0")
    @Max(value = 500, message = "Traffic density must be at most 500")
    private int trafficDensity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Average speed must be at least 0")
    @DecimalMax(value = "120.0", inclusive = true, message = "Average speed must be at most 120")
    private float avgSpeed;

    @Pattern(
            regexp = "Low|Moderate|High|Severe",
            message = "Congestion level must be one of: Low, Moderate, High, Severe"
    )
    private String congestionLevel;

    public TrafficTypeData() {}

    public TrafficTypeData(String location, LocalDateTime timestamp, int trafficDensity, float avgSpeed, String congestionLevel) {
        this.location = location;
        this.timestamp = timestamp;
        this.trafficDensity = trafficDensity;
        this.avgSpeed = avgSpeed;
        this.congestionLevel = congestionLevel;
    }
}
