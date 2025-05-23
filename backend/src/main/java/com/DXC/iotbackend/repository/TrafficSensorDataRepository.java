package com.DXC.iotbackend.repository;



import com.DXC.iotbackend.model.TrafficTypeData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrafficSensorDataRepository extends JpaRepository<TrafficTypeData, UUID> {
}

