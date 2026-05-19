package com.gym_project.messaging;

import com.gym_project.config.JmsConfig;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private WorkloadMessageProducer producer;

    private WorkloadMessage buildMessage(String txId) {
        return WorkloadMessage.builder()
                .trainerUsername("John.Smith")
                .trainerFirstName("John")
                .trainerLastName("Smith")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 4, 15))
                .trainingDuration(60)
                .actionType(WorkloadMessage.ActionType.ADD)
                .transactionId(txId)
                .build();
    }

    @Test
    void send_shouldCallConvertAndSend_withCorrectQueue() {
        WorkloadMessage message = buildMessage("tx-123");

        producer.send(message);

        verify(jmsTemplate).convertAndSend(
                eq(JmsConfig.WORKLOAD_QUEUE),
                eq(message),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    void send_shouldSetTypeHeader_onJmsMessage() throws Exception {
        WorkloadMessage message = buildMessage("tx-123");

        ArgumentCaptor<MessagePostProcessor> captor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        producer.send(message);

        verify(jmsTemplate).convertAndSend(
                eq(JmsConfig.WORKLOAD_QUEUE),
                eq(message),
                captor.capture()
        );

        Message jmsMsg = mock(Message.class);
        captor.getValue().postProcessMessage(jmsMsg);

        verify(jmsMsg).setStringProperty("_type", JmsConfig.TYPE_ID);
    }

    @Test
    void send_shouldSetTransactionIdHeader_whenPresent() throws Exception {
        WorkloadMessage message = buildMessage("tx-abc");

        ArgumentCaptor<MessagePostProcessor> captor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        producer.send(message);

        verify(jmsTemplate).convertAndSend(
                eq(JmsConfig.WORKLOAD_QUEUE),
                eq(message),
                captor.capture()
        );

        Message jmsMsg = mock(Message.class);
        captor.getValue().postProcessMessage(jmsMsg);

        verify(jmsMsg).setStringProperty("transactionId", "tx-abc");
    }

    @Test
    void send_shouldNotSetTransactionIdHeader_whenTransactionIdIsNull() throws Exception {
        WorkloadMessage message = buildMessage(null);

        ArgumentCaptor<MessagePostProcessor> captor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        producer.send(message);

        verify(jmsTemplate).convertAndSend(
                eq(JmsConfig.WORKLOAD_QUEUE),
                eq(message),
                captor.capture()
        );

        Message jmsMsg = mock(Message.class);
        captor.getValue().postProcessMessage(jmsMsg);

        verify(jmsMsg, never()).setStringProperty(eq("transactionId"), any());
    }

    @Test
    void send_shouldNotPropagateException_whenJmsTemplateFails() {
        WorkloadMessage message = buildMessage("tx-123");

        doThrow(new RuntimeException("Broker unavailable"))
                .when(jmsTemplate).convertAndSend(any(String.class), any(Object.class), any(MessagePostProcessor.class));

        assertThatNoException().isThrownBy(() -> producer.send(message));
    }

    @Test
    void send_shouldInvokeProducer_forDeleteAction() {
        WorkloadMessage message = WorkloadMessage.builder()
                .trainerUsername("John.Smith")
                .trainingDate(LocalDate.of(2025, 4, 15))
                .trainingDuration(60)
                .actionType(WorkloadMessage.ActionType.DELETE)
                .transactionId("tx-del")
                .build();

        producer.send(message);

        verify(jmsTemplate).convertAndSend(
                eq(JmsConfig.WORKLOAD_QUEUE),
                eq(message),
                any(MessagePostProcessor.class)
        );
    }
}
