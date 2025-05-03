package com.dxc.iotbackend.controller;



import com.dxc.iotbackend.model.SensorData;
import com.dxc.iotbackend.service.SensorDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensor")
@CrossOrigin
public class SensorDataController {

    private final SensorDataService service;

    public SensorDataController(SensorDataService service) {
        this.service = service;
    }

    @PostMapping("/{type}")
    public SensorData generateSensorData(@PathVariable String type) {
        return service.generateAndSaveRandomSensorData(type);
    }
}
