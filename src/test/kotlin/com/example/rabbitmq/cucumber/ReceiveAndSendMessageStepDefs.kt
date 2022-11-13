package com.example.rabbitmq.cucumber

import com.example.rabbitmq.configuration.RabbitMqTestConfig.RabbitMqTestConstants.RECEIVER_EXCHANGE_NAME
import com.example.rabbitmq.configuration.RabbitMqTestConfig.RabbitMqTestConstants.RECEIVER_ROUTING_KEY
import com.example.rabbitmq.domain.dtos.RabbitMqMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.cucumber.datatable.DataTable
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.*
import java.util.concurrent.TimeUnit

class ReceiveAndSendMessageStepDefs(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitAdmin: RabbitAdmin
) {
    @Given("the following messages exist in the queue")
    fun theFollowingMessagesExistInTheQueue(table: DataTable) {
        testMessages = table.tableConverter.toList(table, RabbitMqMessage::class.java)
    }

    @Given("the {string} exchange does not exist")
    fun theExchangeDoesNotExist(exchangeName: String) {
        rabbitAdmin.deleteExchange(exchangeName)
    }

    @When("the application processes the messages")
    fun theApplicationProcessesTheMessages() {
        testMessages.forEach { rabbitMqMessage ->
            rabbitTemplate.convertAndSend(RECEIVER_EXCHANGE_NAME, RECEIVER_ROUTING_KEY, rabbitMqMessage)
        }
    }

    @Then("the following messages exist in the {string} queue")
    fun theFollowingMessagesExistInTheQueue(queueName: String, table: DataTable) {
        val expectedMessages: List<RabbitMqMessage> = table.tableConverter.toList(table, RabbitMqMessage::class.java)

        val actualMessages = mutableListOf<RabbitMqMessage>()

        await.atMost(15, TimeUnit.SECONDS).untilAsserted {
            val message = rabbitTemplate.receive(queueName)

            if (message != null) {
                val actualMessage = ObjectMapper().readValue<RabbitMqMessage>(message.body)
                actualMessages.add(actualMessage)
            }

            assertEquals(expectedMessages.size, actualMessages.size)
        }

        assertEquals(expectedMessages.sortedBy { it.uuid }, actualMessages.sortedBy { it.uuid })
    }

    @DataTableType
    fun mapRabbitMqMessage(tableRow: Map<String, String>): RabbitMqMessage {
        return RabbitMqMessage(
            data = tableRow["data"].toString(),
            uuid = UUID.fromString(tableRow["uuid"])
        )
    }

    private var testMessages: List<RabbitMqMessage> = emptyList()
}
