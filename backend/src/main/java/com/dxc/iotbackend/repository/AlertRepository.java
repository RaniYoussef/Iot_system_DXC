package com.dxc.iotbackend.repository;


import com.dxc.iotbackend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {}

