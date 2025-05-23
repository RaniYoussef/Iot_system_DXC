package com.DXC.iotbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Data
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String type; // e.g., "Traffic", "Air_Pollution", "Street_Light"
    private String metric; // e.g., "trafficDensity"
    private float value; // The actual value that violated the threshold
    private String location;
    private LocalDateTime timestamp;
}

