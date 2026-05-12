package com.gym_project.messaging;

import com.gym_project.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;

    public void send(WorkloadMessage message) {
        try {
            jmsTemplate.convertAndSend(JmsConfig.WORKLOAD_QUEUE, message,
                    jmsMessage -> {
                        jmsMessage.setStringProperty("_type", JmsConfig.TYPE_ID);
                        if (message.getTransactionId() != null) {
                            jmsMessage.setStringProperty("transactionId",
                                    message.getTransactionId());
                        }
                        return jmsMessage;
                    });

            log.info("[{}] Workload message sent to queue '{}': trainer={}, action={}",
                    message.getTransactionId(),
                    JmsConfig.WORKLOAD_QUEUE,
                    message.getTrainerUsername(),
                    message.getActionType());

        } catch (Exception e) {
            log.error("[{}] Failed to send workload message for trainer={}: {}",
                    message.getTransactionId(),
                    message.getTrainerUsername(),
                    e.getMessage());
        }
    }
}
