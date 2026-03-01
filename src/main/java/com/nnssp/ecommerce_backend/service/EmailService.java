package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.dto.OrderEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@lombok.extern.slf4j.Slf4j
public class EmailService {

    @RabbitListener(queues = "orderQueue")
    public void sendOrderConfirmation(OrderEvent event) {
        // Simulate email sending delay
        try {
            Thread.sleep(2000); // Wait 2 seconds
            log.info("==================================================");
            log.info("📧 EMAIL SENT to {}", event.getEmail());
            log.info("📝 Order ID: {} Confirmed!", event.getOrderId());
            log.info("==================================================");
        } catch (InterruptedException e) {
            log.error("Email sending interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}