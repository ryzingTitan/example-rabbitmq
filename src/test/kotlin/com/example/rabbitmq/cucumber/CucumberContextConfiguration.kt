package com.example.rabbitmq.cucumber

import com.example.rabbitmq.ExampleRabbitMqApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@CucumberContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(
    classes = [ExampleRabbitMqApplication::class]
)
class CucumberContextConfiguration
