package com.dxc.iotbackend.repository;

import com.DXC.iotbackend.model.AirPollutionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AirPollutionDataRepository extends JpaRepository<AirPollutionData, UUID>, JpaSpecificationExecutor<AirPollutionData> {
}
