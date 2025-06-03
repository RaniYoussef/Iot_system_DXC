package com.DXC.iotbackend.controller;

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

//    @GetMapping("${sensor.with.alert}")
//    public Page<DTO> getReadingsWithAlerts(
//            @RequestParam(required = false) String filter1,
//            @RequestParam(required = false) String filter2,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
//            @RequestParam(defaultValue = "timestamp") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        return getService().getReadingsWithAlertInfo(filter1, filter2, start, end, sortBy, sortDir, pageable);
//    }

    @GetMapping("${senor.location}")
    public List<String> getDistinctLocations() {
        return getService().getDistinctLocations();
    }
}