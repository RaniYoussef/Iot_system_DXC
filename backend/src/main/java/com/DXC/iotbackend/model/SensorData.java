package com.DXC.iotbackend.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorType; // e.g., traffic, air, light
    private double value;
    private LocalDateTime timestamp;

    // Constructors
    public SensorData() {}

    public SensorData(String sensorType, double value, LocalDateTime timestamp) {
        this.sensorType = sensorType;
        this.value = value;
        this.timestamp = timestamp;
    }


}

