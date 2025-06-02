package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.StreetLightData;
import com.DXC.iotbackend.payload.StreetLightReadingWithAlertDTO;
import com.DXC.iotbackend.service.StreetLightSensorDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base-path}${sensor.light-path}") // /api/light-sensor
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

    @GetMapping("${sensor.with.alert}") // /with-alerts
    public List<StreetLightReadingWithAlertDTO> getStreetLightWithAlerts(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {
        return service.getStreetLightReadingsWithAlertInfo(location, status, start, end, sortBy, sortDir);
    }

    @GetMapping("${senor.location}") // /location
    public List<String> getDistinctLocations() {
        return service.getDistinctLocations();
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return errors;
        }
    }








    @GetMapping("/with-alertss")
    public Page<StreetLightReadingWithAlertDTO> getSSStreetLightReadingsWithAlerts(
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
        return service.getSStreetLightReadingsWithAlertInfo(
                location, status, start, end, sortBy, sortDir, pageable
        );
    }
}