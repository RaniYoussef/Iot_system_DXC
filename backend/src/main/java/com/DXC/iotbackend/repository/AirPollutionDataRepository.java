package com.DXC.iotbackend.repository;

import com.DXC.iotbackend.model.AirPollutionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AirPollutionDataRepository extends JpaRepository<AirPollutionData, UUID> {
}
