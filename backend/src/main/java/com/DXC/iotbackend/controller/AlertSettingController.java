package com.dxc.iotbackend.controller;

import com.DXC.iotbackend.model.AlertSetting;
import com.DXC.iotbackend.service.AlertSettingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}${alert-settings-path}") // e.g. /api/alert-settings
@CrossOrigin(origins = {"http://localhost:4200", "https://your-trusted-domain.com"}) // ✅ whitelist trusted origins
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

    @GetMapping("${alert.filtering}") // e.g. /filter
    public List<AlertSetting> getFilteredSettings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String metric
    ) {
        return service.getFiltered(type, metric);
    }
}
