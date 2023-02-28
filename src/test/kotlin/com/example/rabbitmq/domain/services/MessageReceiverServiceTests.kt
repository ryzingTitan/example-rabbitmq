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
import org.mockito.kotlin.refEq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class MessageReceiverServiceTests {
    @BeforeEach
    fun setup() {
        messageReceiverService = MessageReceiverService(mockLoggingService, mockMessageSenderService)
    }

    @Nested
    inner class Receive {
        @Test
        fun `logs an info message when successful`() {
            messageReceiverService.receive(testMessage)

            verify(mockLoggingService, times(1)).info(
                any(),
                eq(messageUuid),
                eq("Received message with UUID $messageUuid"),
            )
            verify(mockMessageSenderService, times(1)).send(refEq(expectedSentMessage))
            verify(mockLoggingService, never()).error(any(), any(), any(), any())
        }

        @Test
        fun `logs an error message when an error occurs`() {
            val expectedException = NullPointerException("test exception")
            whenever(mockLoggingService.info(any(), any(), any())).thenThrow(expectedException)

            val exception = assertThrows<NullPointerException> { messageReceiverService.receive(testMessage) }

            assertEquals(expectedException.message, exception.message)

            verify(mockLoggingService, times(1)).info(
                any(),
                eq(messageUuid),
                eq("Received message with UUID $messageUuid"),
            )
            verify(mockMessageSenderService, never()).send(any())
            verify(mockLoggingService, times(1)).error(
                any(),
                eq(messageUuid),
                eq("Message failed to process"),
                eq(expectedException),
            )
        }
    }

    private lateinit var messageReceiverService: MessageReceiverService

    private val mockLoggingService = mock<LoggingService>()
    private val mockMessageSenderService = mock<MessageSenderService>()
    private val messageUuid = UUID.fromString("209c8fd8-6b52-4186-8e3b-5abf750f6fb2")
    private val expectedSentMessage = RabbitMqMessage(
        data = "Sent Message",
        uuid = messageUuid,
    )
    private val testMessage = RabbitMqMessage(
        data = "Test Message",
        uuid = messageUuid,
    )
}
