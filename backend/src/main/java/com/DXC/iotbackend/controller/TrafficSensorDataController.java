package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.TrafficTypeData;
import com.DXC.iotbackend.payload.TrafficReadingWithAlertDTO;
import com.DXC.iotbackend.service.TrafficSensorDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base-path}${sensor.traffic-path}")
@CrossOrigin
public class TrafficSensorDataController extends BaseSensorDataController<TrafficTypeData, TrafficReadingWithAlertDTO> {

    private final TrafficSensorDataService service;

    public TrafficSensorDataController(TrafficSensorDataService service) {
        this.service = service;
    }

    @Override
    protected TrafficSensorDataService getService() {
        return service;
    }

    @GetMapping("${sensor.with.alert}")
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

//@GetMapping
//    public Page<TrafficTypeData> getFilteredData(
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String congestionLevel,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @PageableDefault(size = 5, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
//    ) {
//        return service.getFilteredTrafficData(location, congestionLevel, start, end, pageable);
//    }

//    @GetMapping("${sensor.with.alert}") // /with-alerts
//    public List<TrafficReadingWithAlertDTO> getTrafficWithAlerts(
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String congestionLevel,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
//            @RequestParam(required = false, defaultValue = "desc") String sortDir
//    ) {
//        return service.getTrafficReadingsWithAlertInfo(location, congestionLevel, start, end, sortBy, sortDir);
//    }