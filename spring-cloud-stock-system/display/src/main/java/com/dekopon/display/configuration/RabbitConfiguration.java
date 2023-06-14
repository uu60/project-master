package com.dekopon.display.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dekopon
 * @since 2023/6/13 20:31
 */
@Configuration
@EnableRabbit
public class RabbitConfiguration {

    @Autowired
    private AmqpAdmin amqpAdmin;

    public static final String K_DATA_QUERY_QUEUE_NAME = "k-data-query";
    public static final String K_DATA_RESULT_QUEUE_NAME = "k-data-result";

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void createKDataQueryQueue() {
        Queue queue = new Queue(K_DATA_QUERY_QUEUE_NAME, true, false, false);
        amqpAdmin.declareQueue(queue);

        queue = new Queue(K_DATA_RESULT_QUEUE_NAME, true, false, false);
        amqpAdmin.declareQueue(queue);
    }

}
