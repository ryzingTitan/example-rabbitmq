package com.example.rabbitmq.domain.services

import com.example.rabbitmq.domain.configuration.K8sProperties
import com.example.rabbitmq.domain.configuration.SpringApplicationProperties
import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.UUID

@Service
class LoggingService(
    private val k8sProperties: K8sProperties,
    private val springApplicationProperties: SpringApplicationProperties,
) {
    fun info(logger: Logger, uniqueIdentifier: UUID, message: String) {
        logger.info(
            Markers.append("uniqueIdentifier", uniqueIdentifier)
                .and<LogstashMarker>(Markers.append("applicationName", springApplicationProperties.name))
                .and<LogstashMarker>(Markers.append("podName", k8sProperties.podName))
                .and(Markers.append("nodeName", k8sProperties.nodeName)),
            message,
        )
    }

    fun error(logger: Logger, uniqueIdentifier: UUID, message: String, exception: Exception) {
        logger.error(
            Markers.append("uniqueIdentifier", uniqueIdentifier)
                .and<LogstashMarker>(Markers.append("applicationName", springApplicationProperties.name))
                .and<LogstashMarker>(Markers.append("podName", k8sProperties.podName))
                .and(Markers.append("nodeName", k8sProperties.nodeName)),
            message,
            exception,
        )
    }
}
