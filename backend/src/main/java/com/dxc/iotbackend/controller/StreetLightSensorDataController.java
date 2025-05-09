package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.service.StreetLightSensorDataService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/light-sensor")
@CrossOrigin
public class StreetLightSensorDataController {

    private final StreetLightSensorDataService service;

    public StreetLightSensorDataController(StreetLightSensorDataService service) {
        this.service = service;
    }

    @PostMapping
    public StreetLightData saveData(@Valid @RequestBody StreetLightData data) {
        return service.saveData(data);
    }

    @GetMapping
    public List<StreetLightData> getAllData() {
        return service.getAllData();
    }
}
