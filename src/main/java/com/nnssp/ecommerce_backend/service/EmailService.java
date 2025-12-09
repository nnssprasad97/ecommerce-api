package com.nnssp.ecommerce_backend.service;

import com.nnssp.ecommerce_backend.dto.OrderEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @RabbitListener(queues = "orderQueue")
    public void sendOrderConfirmation(OrderEvent event) {
        // Simulate email sending delay
        try {
            Thread.sleep(2000); // Wait 2 seconds
            System.out.println("==================================================");
            System.out.println("📧 EMAIL SENT to " + event.getEmail());
            System.out.println("📝 Order ID: " + event.getOrderId() + " Confirmed!");
            System.out.println("==================================================");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}