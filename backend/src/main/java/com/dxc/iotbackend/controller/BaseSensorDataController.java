package com.dxc.iotbackend.controller;

import com.dxc.iotbackend.service.BaseSensorDataService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseSensorDataController<E, D> {

    protected abstract BaseSensorDataService<E, D> getService();

    @PostMapping
    public E saveData(@Valid @RequestBody E data) {
        return getService().saveData(data);
    }

    @GetMapping("${senor.location}")
    public List<String> getDistinctLocations() {
        return getService().getDistinctLocations();
    }
}
