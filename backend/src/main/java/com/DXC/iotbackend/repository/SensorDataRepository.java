package com.dxc.iotbackend.repository;


import com.dxc.iotbackend.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorData, Long>
{
}

