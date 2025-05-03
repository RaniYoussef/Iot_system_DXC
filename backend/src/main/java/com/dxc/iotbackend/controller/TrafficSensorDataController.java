package com.dxc.iotbackend.controller;



import com.dxc.iotbackend.model.TrafficTypeData;
import com.dxc.iotbackend.service.TrafficSensorDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic-sensor")
@CrossOrigin
public class TrafficSensorDataController {

    private final TrafficSensorDataService service;

    public TrafficSensorDataController(TrafficSensorDataService service) {
        this.service = service;
    }

    @PostMapping
    public TrafficTypeData saveData(@Valid @RequestBody TrafficTypeData data) {
        return service.saveData(data);
    }


    @GetMapping
    public List<TrafficTypeData> getAllData() {
        return service.getAllData();
    }
}

