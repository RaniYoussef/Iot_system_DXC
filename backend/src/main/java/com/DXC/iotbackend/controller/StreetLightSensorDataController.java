package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.model.StreetLightData;
import com.dxc.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.dxc.iotbackend.service.StreetLightSensorDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}${sensor.light-path}")
@CrossOrigin
public class StreetLightSensorDataController extends BaseSensorDataController<StreetLightData, StreetLightReadingWithAlertDTO> {

    private final StreetLightSensorDataService service;

    public StreetLightSensorDataController(StreetLightSensorDataService service) {
        this.service = service;
    }

    @Override
    protected StreetLightSensorDataService getService() {
        return service;
    }


    @GetMapping("${sensor.with.alert}")
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


//    @GetMapping
//    public Page<StreetLightData> getFilteredStreetLightData(
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @PageableDefault(size = 5, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
//    ) {
//        return service.getFilteredStreetLightData(location, status, start, end, pageable);
//    }
//
//    @GetMapping("${sensor.with.alert}") // /with-alerts
//    public List<StreetLightReadingWithAlertDTO> getStreetLightWithAlerts(
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
//            @RequestParam(required = false, defaultValue = "desc") String sortDir
//    ) {
//        return service.getStreetLightReadingsWithAlertInfo(location, status, start, end, sortBy, sortDir);
//    }