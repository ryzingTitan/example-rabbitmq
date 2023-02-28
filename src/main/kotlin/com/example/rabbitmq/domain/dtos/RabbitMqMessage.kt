package com.example.rabbitmq.domain.dtos

import lombok.Generated
import java.util.UUID

@Generated
data class RabbitMqMessage(
    val data: String = "",
    val uuid: UUID = UUID.randomUUID(),
)
