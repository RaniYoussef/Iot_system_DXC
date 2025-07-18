package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.dxc.iotbackend.service.StreetLightSensorDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}${sensor.light-path}") // e.g. /api/street-light-sensor
@CrossOrigin(origins = {"http://localhost:4200", "https://your-trusted-domain.com"}) // ✅ restrict CORS to trusted domains
public class StreetLightSensorDataController
        extends BaseSensorDataController<StreetLightData, StreetLightReadingWithAlertDTO> {

    private final StreetLightSensorDataService service;

    public StreetLightSensorDataController(StreetLightSensorDataService service) {
        this.service = service;
    }

    @Override
    protected StreetLightSensorDataService getService() {
        return service;
    }

    @GetMapping("${sensor.with.alert}") // e.g. /with-alerts
    public Page<StreetLightReadingWithAlertDTO> getStreetLightReadings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return getService().getReadingsWithAlertInfo(location, status, start, end, sortBy, sortDir, pageable);
    }
}
