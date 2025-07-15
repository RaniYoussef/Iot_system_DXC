package com.DXC.iotbackend.payload;

import com.DXC.iotbackend.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirPollutionReadingWithAlertDTO {
    private UUID id;
    private String location;
    private LocalDateTime timestamp;
    private float co;
    private float ozone;
    private String pollutionLevel;
    private LocalDateTime alertTimestamp;
    private List<Alert> alerts;
}