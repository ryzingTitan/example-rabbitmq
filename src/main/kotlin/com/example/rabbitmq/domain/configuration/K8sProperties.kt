package com.example.rabbitmq.domain.configuration

import lombok.Generated
import org.springframework.boot.context.properties.ConfigurationProperties

@Generated
@ConfigurationProperties(prefix = "k8s")
data class K8sProperties(
    val podName: String,
    val nodeName: String,
)
