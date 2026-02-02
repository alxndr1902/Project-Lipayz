package com.zezame.lipayz.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EMAIL_EX_ACTIVATION = "email.notification.exchange.activation";
    public static final String EMAIL_QUEUE_ACTIVATION = "email.notification.queue.activation";
    public static final String EMAIL_ROUTING_KEY_ACTIVATION = "email.notification.key.activation";

    public static final String EMAIL_EX_CREATE_TRANSACTION = "email.notification.exchange.create.transaction";
    public static final String EMAIL_QUEUE_CREATE_TRANSACTION = "email.notification.queue.create.transaction";
    public static final String EMAIL_ROUTING_KEY_CREATE_TRANSACTION = "email.notification.key.create.transaction";

    public static final String EMAIL_EX_UPDATE_TRANSACTION = "email.notification.exchange.update.transaction";
    public static final String EMAIL_QUEUE_UPDATE_TRANSACTION = "email.notification.queue.update.transaction";
    public static final String EMAIL_ROUTING_KEY_UPDATE_TRANSACTION = "email.notification.key.update.transaction";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange activationExchange() {
        return new DirectExchange(EMAIL_EX_ACTIVATION);
    }

    @Bean
    public Queue activationQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE_ACTIVATION).build();
    }

    @Bean
    public Binding activationBinding() {
        return BindingBuilder.bind(activationQueue())
                .to(activationExchange())
                .with(EMAIL_ROUTING_KEY_ACTIVATION);
    }

    @Bean
    public DirectExchange createTransactionExchange() {
        return new DirectExchange(EMAIL_EX_CREATE_TRANSACTION);
    }

    @Bean
    public Queue createTransactionQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE_CREATE_TRANSACTION).build();
    }

    @Bean
    public Binding createTransactionBinding() {
        return BindingBuilder.bind(createTransactionQueue())
                .to(createTransactionExchange())
                .with(EMAIL_ROUTING_KEY_CREATE_TRANSACTION);
    }

    @Bean
    public DirectExchange updateTransactionExchange() {
        return new DirectExchange(EMAIL_EX_UPDATE_TRANSACTION);
    }

    @Bean
    public Queue updateTransactionQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE_UPDATE_TRANSACTION).build();
    }

    @Bean
    public Binding updateTransactionBinding() {
        return BindingBuilder.bind(updateTransactionQueue())
                .to(updateTransactionExchange())
                .with(EMAIL_ROUTING_KEY_UPDATE_TRANSACTION);
    }
}
