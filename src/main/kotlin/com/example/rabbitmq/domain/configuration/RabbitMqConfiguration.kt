package com.example.rabbitmq.domain.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfiguration {
    @Bean
    fun configureMessageConverter() = Jackson2JsonMessageConverter(
        ObjectMapper().registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(DEFAULT_REFLECTION_CACHE_SIZE)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
    )

    companion object RabbitMqConfigurationConstants {
        private const val DEFAULT_REFLECTION_CACHE_SIZE = 512
    }
}
