package com.DXC.iotbackend.repository;

import com.DXC.iotbackend.model.StreetLightData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StreetLightSensorDataRepository extends JpaRepository<StreetLightData, UUID> {
}
