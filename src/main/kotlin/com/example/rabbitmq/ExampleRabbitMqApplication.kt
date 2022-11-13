package com.example.rabbitmq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ExampleRabbitMqApplication

fun main(args: Array<String>) {
    runApplication<ExampleRabbitMqApplication>(arrayOf(args).toString())
}
