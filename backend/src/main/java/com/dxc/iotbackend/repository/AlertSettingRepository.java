package com.dxc.iotbackend.repository;

import com.dxc.iotbackend.model.AlertSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertSettingRepository extends JpaRepository<AlertSetting, UUID> {
}
