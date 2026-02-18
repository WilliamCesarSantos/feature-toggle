package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import org.slf4j.LoggerFactory
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class ConfigRefreshEventConsumer(
    private val contextRefresher: ContextRefresher
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean("configRefreshConsumer")
    fun configRefreshConsumer(): Consumer<Message<String>> {
        return Consumer { message ->
            try {
                logger.info("Received config refresh event from Kafka")
                logger.debug("Payload: {}, Headers: {}", message.payload, message.headers)

                logger.debug("Starting context refresh")
                contextRefresher.refresh()

                logger.info("Context refreshed successfully")
            } catch (e: Exception) {
                logger.error("Error processing refresh event", e)
            }
        }
    }

}

