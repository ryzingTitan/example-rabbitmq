package com.example.rabbitmq.domain.services

import com.example.rabbitmq.domain.dtos.RabbitMqMessage
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class MessageReceiverService(
    private val loggingService: LoggingService,
    private val messageSenderService: MessageSenderService
) {
    private val logger = LoggerFactory.getLogger(MessageSenderService::class.java)

    @RabbitListener(queues = ["example_receiver"])
    @Suppress("TooGenericExceptionCaught")
    fun receive(@Payload receivedMessage: RabbitMqMessage) {
        try {
            loggingService.info(logger, receivedMessage.uuid, "Received message with UUID ${receivedMessage.uuid}")

            val sentMessage = RabbitMqMessage("Sent Message", receivedMessage.uuid)
            messageSenderService.send(sentMessage)
        } catch (exception: Exception) {
            loggingService.error(logger, receivedMessage.uuid, "Message failed to process", exception)
            throw exception
        }
    }
}
