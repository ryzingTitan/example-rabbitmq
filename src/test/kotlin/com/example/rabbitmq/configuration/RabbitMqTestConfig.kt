package com.example.rabbitmq.configuration

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqTestConfig {
    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(MockConnectionFactory())
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE)
        return connectionFactory
    }

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        val rabbitAdmin = RabbitAdmin(connectionFactory)
        setupRabbitMq(rabbitAdmin)
        return rabbitAdmin
    }

    private fun setupRabbitMq(rabbitAdmin: RabbitAdmin) {
        val queueSettings = HashMap<String, Any>()
        queueSettings.putIfAbsent("x-dead-letter-exchange", RECEIVER_ERROR_EXCHANGE_NAME)
        queueSettings.putIfAbsent("x-dead-letter-routing-key", RECEIVER_ERROR_ROUTING_KEY)

        val receiverQueue = Queue(RECEIVER_QUEUE_NAME, false, false, false, queueSettings)
        val receiverExchange = DirectExchange(RECEIVER_EXCHANGE_NAME)
        setupExchange(rabbitAdmin, receiverQueue, receiverExchange, RECEIVER_ROUTING_KEY)

        val receiverErrorQueue = Queue(RECEIVER_ERROR_QUEUE_NAME, false, false, false, queueSettings)
        val receiverErrorExchange = DirectExchange(RECEIVER_ERROR_EXCHANGE_NAME)
        setupExchange(rabbitAdmin, receiverErrorQueue, receiverErrorExchange, RECEIVER_ERROR_ROUTING_KEY)

        val senderQueue = Queue(SENDER_QUEUE_NAME, false, false, false, queueSettings)
        val senderExchange = DirectExchange(SENDER_EXCHANGE_NAME)
        setupExchange(rabbitAdmin, senderQueue, senderExchange, SENDER_ROUTING_KEY)
    }

    private fun setupExchange(rabbitAdmin: RabbitAdmin, queue: Queue, exchange: DirectExchange, routingKey: String) {
        rabbitAdmin.declareQueue(queue)
        rabbitAdmin.declareExchange(exchange)
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey))
    }

    companion object RabbitMqTestConstants {
        const val RECEIVER_EXCHANGE_NAME = "example_receiver"
        const val RECEIVER_ROUTING_KEY = "example_receiver"
        private const val RECEIVER_QUEUE_NAME = "example_receiver"
        private const val RECEIVER_ERROR_QUEUE_NAME = "example_receiver_error"
        private const val RECEIVER_ERROR_EXCHANGE_NAME = "example_receiver_error"
        private const val RECEIVER_ERROR_ROUTING_KEY = "example_receiver_error"
        private const val SENDER_QUEUE_NAME = "example_sender"
        private const val SENDER_EXCHANGE_NAME = "example_sender"
        private const val SENDER_ROUTING_KEY = "example_sender"
    }
}
