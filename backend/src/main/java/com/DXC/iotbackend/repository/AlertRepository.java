package com.DXC.iotbackend.repository;


import com.DXC.iotbackend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {}

