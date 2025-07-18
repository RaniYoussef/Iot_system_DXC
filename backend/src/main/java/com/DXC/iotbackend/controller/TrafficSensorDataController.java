package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.service.TrafficSensorDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}${sensor.traffic-path}") // e.g. /api/traffic-sensor
@CrossOrigin(origins = {"http://localhost:4200", "https://your-trusted-domain.com"}) // ✅ whitelist trusted frontend origins
public class TrafficSensorDataController
        extends BaseSensorDataController<TrafficTypeData, TrafficReadingWithAlertDTO> {

    private final TrafficSensorDataService service;

    public TrafficSensorDataController(TrafficSensorDataService service) {
        this.service = service;
    }

    @Override
    protected TrafficSensorDataService getService() {
        return service;
    }

    @GetMapping("${sensor.with.alert}") // e.g. /with-alerts
    public Page<TrafficReadingWithAlertDTO> getTrafficReadings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String congestionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return getService().getReadingsWithAlertInfo(location, congestionLevel, start, end, sortBy, sortDir, pageable);
    }
}
