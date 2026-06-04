package edu.udla.integracion.progreso2.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue billingQueue() {
        return new Queue("billing.queue", true);
    }

    @Bean
    public FanoutExchange appointmentsExchange() {
        return new FanoutExchange("appointments.events");
    }

    @Bean
    public Queue notificationsQueue() {
        return new Queue("notifications.queue", true);
    }

    @Bean
    public Queue analyticsQueue() {
        return new Queue("analytics.queue", true);
    }

    @Bean
    public Binding bindNotifications() {
        return BindingBuilder.bind(notificationsQueue()).to(appointmentsExchange());
    }

    @Bean
    public Binding bindAnalytics() {
        return BindingBuilder.bind(analyticsQueue()).to(appointmentsExchange());
    }
}