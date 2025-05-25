package com.DXC.iotbackend.controller;



import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.service.TrafficSensorDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;

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
    public Page<TrafficTypeData> getFilteredData(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String congestionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 5, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getFilteredTrafficData(location, congestionLevel, start, end, pageable);
    }

    @GetMapping("/with-alerts")
    public List<TrafficReadingWithAlertDTO> getTrafficWithAlerts(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String congestionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        return service.getTrafficReadingsWithAlertInfo(location, congestionLevel, start, end, sortBy, sortDir);
    }

    @GetMapping("/locations")
    public List<String> getDistinctLocations() {
        return service.getDistinctLocations();
    }



}


