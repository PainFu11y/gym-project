package com.gym_report.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@Component
@Slf4j
public class WorkloadDeadLetterConsumer {

    @JmsListener(destination = "ActiveMQ.DLQ",
                 containerFactory = "jmsListenerContainerFactory")
    public void handleDeadLetter(Message message) {
        try {
            String body = message instanceof TextMessage textMsg
                    ? textMsg.getText()
                    : "[non-text message]";

            String originalDest = message.getStringProperty("dlqDeliveryFailureCause") != null
                    ? message.getStringProperty("dlqDeliveryFailureCause")
                    : "unknown";

            log.error("DEAD LETTER received — original destination: {}, failure: {}, body: {}",
                    message.getJMSDestination(),
                    originalDest,
                    body);

        } catch (Exception e) {
            log.error("Failed to process dead letter message: {}", e.getMessage());
        }
    }
}
