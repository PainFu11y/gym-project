package com.gym_report.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym_report.messaging.WorkloadMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

    public static final String WORKLOAD_QUEUE = "trainer.workload.queue";
    public static final String DLQ            = "ActiveMQ.DLQ";
    public static final String TYPE_ID        = "WorkloadMessage";

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user:admin}")
    private String brokerUser;

    @Value("${spring.activemq.password:admin}")
    private String brokerPassword;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(brokerUser, brokerPassword, brokerUrl);

        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setMaximumRedeliveries(3);
        policy.setInitialRedeliveryDelay(1000L);
        policy.setBackOffMultiplier(2.0);
        policy.setUseExponentialBackOff(true);
        factory.setRedeliveryPolicy(policy);

        factory.setTrustedPackages(List.of("com.gym_report.messaging"));

        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of(TYPE_ID, WorkloadMessage.class));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(mapper);

        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setSessionAcknowledgeMode(jakarta.jms.Session.CLIENT_ACKNOWLEDGE);
        factory.setConcurrency("1-5");
        factory.setErrorHandler(jmsErrorHandler());
        return factory;
    }

    @Bean
    public ErrorHandler jmsErrorHandler() {
        return t -> log.error("JMS listener error (message will be redelivered/DLQ'd): {}",
                t.getCause() != null ? t.getCause().getMessage() : t.getMessage());
    }
}
