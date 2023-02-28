package com.example.rabbitmq.cucumber.common

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.example.rabbitmq.cucumber.dtos.LogMessage
import com.example.rabbitmq.domain.services.MessageReceiverService
import com.example.rabbitmq.domain.services.MessageSenderService
import io.cucumber.datatable.DataTable
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.LoggerFactory

class LoggingStepDefs {
    @DataTableType
    fun mapLogMessage(tableRow: Map<String, String>): LogMessage {
        return LogMessage(
            level = tableRow["level"].toString(),
            message = tableRow["message"].toString(),
        )
    }

    @Before
    fun setup() {
        messageReceiverLogger = LoggerFactory.getLogger(MessageReceiverService::class.java) as Logger
        messageSenderLogger = LoggerFactory.getLogger(MessageSenderService::class.java) as Logger

        messageReceiverLogger.addAppender(appender)
        messageSenderLogger.addAppender(appender)

        appender.start()
    }

    @Then("the application will log the following messages:")
    fun theApplicationWilLogTheFollowingMessages(table: DataTable) {
        val expectedLogMessages: List<LogMessage> = table.tableConverter.toList(table, LogMessage::class.java)

        val actualLogMessages = ArrayList<LogMessage>()

        appender.list.forEach {
            actualLogMessages.add(LogMessage(it.level.levelStr, it.message))
        }

        assertEquals(expectedLogMessages.sortedBy { it.message }, actualLogMessages.sortedBy { it.message })
    }

    @After
    fun teardown() {
        appender.stop()
    }

    private lateinit var messageReceiverLogger: Logger
    private lateinit var messageSenderLogger: Logger

    private val appender: ListAppender<ILoggingEvent> = ListAppender()
}
