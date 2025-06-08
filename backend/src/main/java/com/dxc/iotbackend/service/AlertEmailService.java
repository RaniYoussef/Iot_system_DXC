package com.dxc.iotbackend.service;

import com.dxc.iotbackend.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to simulate sending email notifications for alerts.
 * Replace this with a real email service in production.
 */
@Service
public class AlertEmailService {

    private static final Logger logger = LoggerFactory.getLogger(AlertEmailService.class);

    public void sendAlertEmail(Alert alert) {
        if (alert == null) {
            logger.warn("Attempted to send alert email, but alert is null.");
            return;
        }

        logger.info("===== SIMULATED EMAIL ALERT =====");
        logger.info("To: admin@example.com");
        logger.info("Subject: New Alert - {} - {}", alert.getType(), alert.getMetric());
        logger.info("Body: An alert was triggered at {} on {}", alert.getLocation(), alert.getTimestamp());
        logger.info("Metric: {} | Value: {}", alert.getMetric(), alert.getValue());
        logger.info("==================================");
    }
}
