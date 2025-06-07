package com.dxc.iotbackend.repository;



import com.dxc.iotbackend.model.TrafficTypeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TrafficSensorDataRepository extends JpaRepository<TrafficTypeData, UUID>, JpaSpecificationExecutor<TrafficTypeData> {
}

