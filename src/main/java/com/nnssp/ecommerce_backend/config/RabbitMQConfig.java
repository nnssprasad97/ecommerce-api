package com.nnssp.ecommerce_backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // This creates a queue named "orderQueue" in RabbitMQ automatically
    @Bean
    public Queue orderQueue() {
        return new Queue("orderQueue", true);
    }
}