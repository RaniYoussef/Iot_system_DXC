package com.DXC.iotbackend.repository;

import com.DXC.iotbackend.model.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertSettingRepository extends JpaRepository<AlertSetting, UUID> {
}
