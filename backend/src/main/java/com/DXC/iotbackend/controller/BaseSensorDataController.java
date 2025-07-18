package com.dxc.iotbackend.controller;

import com.DXC.iotbackend.service.BaseSensorDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseSensorDataController<T, DTO> {

    protected abstract BaseSensorDataService<T, DTO> getService();

    @PostMapping
    public T saveData(@Valid @RequestBody T data) {
        return getService().saveData(data);
    }


    @GetMapping("${senor.location}")
    public List<String> getDistinctLocations() {
        return getService().getDistinctLocations();
    }
}