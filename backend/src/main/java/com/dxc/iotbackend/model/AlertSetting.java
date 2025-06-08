package com.dxc.iotbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "settings")
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String type; // Traffic, Air_Pollution, Street_Light

    @NotBlank
    private String metric;

    @NotNull
    private Float thresholdValue;

    @Pattern(regexp = "above|below", message = "alertType must be 'above' or 'below'")
    private String alertType;

    private LocalDateTime createdAt = LocalDateTime.now(); // auto-set on creation
}
