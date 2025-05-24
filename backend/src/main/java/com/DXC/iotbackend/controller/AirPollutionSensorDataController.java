package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.AirPollutionData;
import com.DXC.iotbackend.service.AirPollutionDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/air-pollution-sensor")
@CrossOrigin
@Valid
public class AirPollutionSensorDataController {

    private final AirPollutionDataService service;

    public AirPollutionSensorDataController(AirPollutionDataService service) {
        this.service = service;
    }

    @PostMapping
    public AirPollutionData saveData(@Valid @RequestBody AirPollutionData data) {
        return service.saveData(data);
    }


    @GetMapping
    public Page<AirPollutionData> getFilteredAirData(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String pollutionLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 5, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getFilteredAirPollutionData(location, pollutionLevel, start, end, pageable);
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


}


