package com.dxc.iotbackend.payload;

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
public class StreetLightReadingWithAlertDTO {
    private UUID id; // ✅ changed from Long to UUID
    private String location;
    private LocalDateTime timestamp;
    private float brightnessLevel;
    private float powerConsumption;
    private String status;
    private LocalDateTime alertTimestamp;
    private List<Alert> alerts; // ✅ contains the full alert info
}
