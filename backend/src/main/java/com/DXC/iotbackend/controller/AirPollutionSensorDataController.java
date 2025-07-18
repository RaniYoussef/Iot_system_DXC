package com.dxc.iotbackend.controller;

import com.DXC.iotbackend.model.AirPollutionData;
import com.DXC.iotbackend.payload.AirPollutionReadingWithAlertDTO;
import com.DXC.iotbackend.service.AirPollutionDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}${sensor.air-path}") // e.g. /api/air-pollution-sensor
@CrossOrigin(origins = {"http://localhost:4200", "https://your-trusted-domain.com"}) // ✅ whitelist trusted domains
public class AirPollutionSensorDataController
        extends BaseSensorDataController<AirPollutionData, AirPollutionReadingWithAlertDTO> {

    private final AirPollutionDataService service;

    public AirPollutionSensorDataController(AirPollutionDataService service) {
        this.service = service;
    }

    @Override
    protected AirPollutionDataService getService() {
        return service;
    }

    @GetMapping("${sensor.with.alert}")
    public Page<AirPollutionReadingWithAlertDTO> getAirPollutionReadings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String pollutionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return getService().getReadingsWithAlertInfo(location, pollutionLevel, start, end, sortBy, sortDir, pageable);
    }
}
