package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.service.StreetLightSensorDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
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
    public Page<StreetLightData> getFilteredStreetLightData(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 5, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getFilteredStreetLightData(location, status, start, end, pageable);
    }
}
