//package com.zezame.lipayz.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//    public static final String EMAIL_EX_CR = "email.notification.exchange.cr";
//    public static final String EMAIL_QUEUE_CR = "email.notification.queue.cr";
//    public static final String EMAIL_ROUTING_KEY_CR = "email.notification.key.cr";
//
//    public static final String EMAIL_EX_UD = "email.notification.exchange.ud";
//    public static final String EMAIL_QUEUE_UD = "email.notification.queue.ud";
//    public static final String EMAIL_ROUTING_KEY_UD = "email.notification.key.ud";
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public DirectExchange checkoutExchange() {
//        return new DirectExchange(EMAIL_EX_CR);
//    }
//
//    @Bean
//    public DirectExchange checkInExchange() {
//        return new DirectExchange(EMAIL_EX_UD);
//    }
//
//    @Bean
//    public Queue checkInQueue() {
//        return QueueBuilder.durable(EMAIL_QUEUE_UD).build();
//    }
//
//    @Bean
//    public Queue checkoutQueue() {
//        return QueueBuilder.durable(EMAIL_QUEUE_CR).build();
//    }
//
//    @Bean
//    public Binding checkoutBinding() {
//        return BindingBuilder.bind(checkoutQueue())
//                .to(checkoutExchange())
//                .with(EMAIL_ROUTING_KEY_CR);
//    }
//
//    @Bean
//    public Binding checkInBinding() {
//        return BindingBuilder.bind(checkInQueue())
//                .to(checkInExchange())
//                .with(EMAIL_ROUTING_KEY_UD);
//    }
//}
