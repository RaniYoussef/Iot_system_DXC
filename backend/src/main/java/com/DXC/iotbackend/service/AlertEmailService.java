package com.dxc.iotbackend.service;

import com.DXC.iotbackend.model.Alert;
import org.springframework.stereotype.Service;

@Service
public class AlertEmailService {

    public void sendAlertEmail(Alert alert) {
        // Simulated email content
        System.out.println("\n===== SIMULATED EMAIL ALERT =====");
        System.out.println("To: admin@example.com");
        System.out.println("Subject: New Alert - " + alert.getType() + " - " + alert.getMetric());
        System.out.println("Body: An alert was triggered at " + alert.getLocation() + " on " + alert.getTimestamp());
        System.out.println("Metric: " + alert.getMetric() + " | Value: " + alert.getValue());
        System.out.println("==================================\n");
    }
}