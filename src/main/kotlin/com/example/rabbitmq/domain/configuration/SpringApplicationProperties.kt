package com.example.rabbitmq.domain.configuration

import lombok.Generated
import org.springframework.boot.context.properties.ConfigurationProperties

@Generated
@ConfigurationProperties(prefix = "spring.application")
data class SpringApplicationProperties(
    val name: String,
)
