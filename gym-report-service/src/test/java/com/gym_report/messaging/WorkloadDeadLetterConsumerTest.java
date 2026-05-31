package com.gym_report.messaging;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadDeadLetterConsumerTest {

    @InjectMocks
    private WorkloadDeadLetterConsumer consumer;


    @Test
    void handleDeadLetter_shouldProcessTextMessage_withFailureCause() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("{\"trainerUsername\":\"john\"}");
        when(message.getStringProperty("dlqDeliveryFailureCause"))
                .thenReturn("MessageConversionException: Could not find type id property");
        when(message.getJMSDestination()).thenReturn(null);

        assertThatNoException().isThrownBy(() -> consumer.handleDeadLetter(message));

        verify(message).getText();
        verify(message, times(2)).getStringProperty("dlqDeliveryFailureCause");
    }

    @Test
    void handleDeadLetter_shouldUseUnknown_whenNoFailureCauseProperty() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("some body");
        when(message.getStringProperty("dlqDeliveryFailureCause")).thenReturn(null);
        when(message.getJMSDestination()).thenReturn(null);

        assertThatNoException().isThrownBy(() -> consumer.handleDeadLetter(message));
    }

    @Test
    void handleDeadLetter_shouldHandleNonTextMessage_withPlaceholderBody() throws Exception {
        Message message = mock(Message.class);   // NOT a TextMessage
        when(message.getStringProperty("dlqDeliveryFailureCause")).thenReturn("some cause");
        when(message.getJMSDestination()).thenReturn(null);

        assertThatNoException().isThrownBy(() -> consumer.handleDeadLetter(message));

        verify(message, never()).getStringProperty("fakeProperty");
    }

    @Test
    void handleDeadLetter_shouldNotThrow_whenJmsExceptionOccurs() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenThrow(new jakarta.jms.JMSException("read error"));

        assertThatNoException().isThrownBy(() -> consumer.handleDeadLetter(message));
    }

    @Test
    void handleDeadLetter_shouldNotThrow_whenGetStringPropertyFails() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("body");
        when(message.getStringProperty("dlqDeliveryFailureCause"))
                .thenThrow(new jakarta.jms.JMSException("property error"));

        assertThatNoException().isThrownBy(() -> consumer.handleDeadLetter(message));
    }
}
