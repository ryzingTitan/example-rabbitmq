package com.example.rabbitmq.domain.services

import com.example.rabbitmq.domain.dtos.RabbitMqMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.amqp.rabbit.core.RabbitOperations
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.*

class MessageSenderServiceTests {
    @BeforeEach
    fun setup() {
        messageSenderService = MessageSenderService(mockLoggingService, mockRabbitTemplate)
    }

    @Nested
    inner class Send {
        @Test
        fun `sends message to queue`() {
            messageSenderService.send(testMessage)

            verify(mockLoggingService, times(1)).info(
                any(),
                eq(testMessage.uuid),
                eq("Sending message with UUID ${testMessage.uuid}"),
            )
            verify(mockRabbitTemplate, times(1)).invoke(any<RabbitOperations.OperationsCallback<RabbitTemplate>>())
            verify(mockLoggingService, never()).error(any(), any(), any(), any())
        }

        @Test
        fun `logs error when message doesn't send`() {
            val expectedException = NullPointerException("test exception")
            whenever(mockLoggingService.info(any(), any(), any())).thenThrow(expectedException)

            val exception = assertThrows<NullPointerException> { messageSenderService.send(testMessage) }

            assertEquals(expectedException.message, exception.message)

            verify(mockLoggingService, times(1)).info(
                any(),
                eq(testMessage.uuid),
                eq("Sending message with UUID ${testMessage.uuid}"),
            )
            verify(mockRabbitTemplate, never()).invoke(any<RabbitOperations.OperationsCallback<RabbitTemplate>>())
            verify(mockLoggingService, times(1)).error(
                any(),
                eq(testMessage.uuid),
                eq("Message failed to send"),
                eq(expectedException),
            )
        }
    }

    private lateinit var messageSenderService: MessageSenderService

    private val mockLoggingService = mock<LoggingService>()
    private val mockRabbitTemplate = mock<RabbitTemplate>()
    private val testMessage = RabbitMqMessage(
        data = "test message",
        uuid = UUID.fromString("8a26b6fa-b30e-41db-886f-4e6d0865af65"),
    )
}
