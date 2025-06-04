package com.DXC.iotbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "street_light_sensor_data")
public class StreetLightData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Location is required")
    private String location;

    @PastOrPresent(message = "Timestamp cannot be in the future")
    private LocalDateTime timestamp;

    @Min(value = 0)
    @Max(value = 100)
    private int brightnessLevel;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5000.0")
    private float powerConsumption;

    @Pattern(regexp = "ON|OFF", message = "Status must be ON or OFF")
    private String status;

    public StreetLightData() {}

    public StreetLightData(String location, LocalDateTime timestamp, int brightnessLevel, float powerConsumption, String status) {
        this.location = location;
        this.timestamp = timestamp;
        this.brightnessLevel = brightnessLevel;
        this.powerConsumption = powerConsumption;
        this.status = status;
    }
}

