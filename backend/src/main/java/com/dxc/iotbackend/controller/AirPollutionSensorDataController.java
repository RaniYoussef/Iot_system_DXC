package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.model.AirPollutionData;
import com.dxc.iotbackend.service.AirPollutionDataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
    public List<AirPollutionData> getAllData() {
        return service.getAllData();
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


