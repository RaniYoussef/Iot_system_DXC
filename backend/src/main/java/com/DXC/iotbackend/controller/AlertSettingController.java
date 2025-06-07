package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.model.AlertSetting;
import com.dxc.iotbackend.service.AlertSettingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestController
@RequestMapping("${api.base-path}${alert-settings-path}") // /api/alert-settings
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

    @GetMapping("${alert.filtering}") // /filter
    public List<AlertSetting> getFilteredSettings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String metric
    ) {
        return service.getFiltered(type, metric);
    }

}
