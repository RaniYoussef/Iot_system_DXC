package com.DXC.iotbackend.controller;

import com.DXC.iotbackend.model.AlertSetting;
import com.DXC.iotbackend.service.AlertSettingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestController
@RequestMapping("/api/alert-settings")
@CrossOrigin

public class AlertSettingController {

    private final AlertSettingService service;

    public AlertSettingController(AlertSettingService service) {
        this.service = service;
    }

    @PostMapping
    public AlertSetting saveSetting(@Valid @RequestBody AlertSetting setting) {
        return service.save(setting);
    }

    @GetMapping
    public List<AlertSetting> getAllSettings() {
        return service.getAll();
    }


    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
