package com.dxc.iotbackend.repository;

import com.dxc.iotbackend.model.AirPollutionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AirPollutionDataRepository extends JpaRepository<AirPollutionData, UUID> {
}
