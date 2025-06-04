package com.DXC.iotbackend.controller;



import com.DXC.iotbackend.model.SensorData;
import com.DXC.iotbackend.service.SensorDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}${sensor.general-path}") // /api/sensor
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
