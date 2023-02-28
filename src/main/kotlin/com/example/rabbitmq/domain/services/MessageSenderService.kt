package com.example.rabbitmq.domain.services

import com.example.rabbitmq.domain.dtos.RabbitMqMessage
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessageSenderService(
    private val loggingService: LoggingService,
    private val rabbitTemplate: RabbitTemplate,
) {
    private val logger = LoggerFactory.getLogger(MessageSenderService::class.java)

    @Suppress("TooGenericExceptionCaught")
    fun send(message: RabbitMqMessage) {
        try {
            loggingService.info(logger, message.uuid, "Sending message with UUID ${message.uuid}")

            rabbitTemplate.invoke {
                it.convertAndSend("example_sender", "example_sender", message) { message ->
                    message.messageProperties.headers.remove("_TypeId_")
                    message
                }
                it.waitForConfirmsOrDie(CONFIRMATION_TIMEOUT)
            }
        } catch (exception: Exception) {
            loggingService.error(logger, message.uuid, "Message failed to send", exception)
            throw exception
        }
    }

    companion object MessageSenderServiceConstants {
        private const val CONFIRMATION_TIMEOUT = 100L
    }
}
