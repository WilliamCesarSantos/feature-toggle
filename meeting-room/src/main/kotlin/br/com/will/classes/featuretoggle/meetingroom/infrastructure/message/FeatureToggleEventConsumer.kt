package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class FeatureToggleEventConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean("toggleUpdatedInput")
    fun toggleUpdatedInput(
        featureToggleState: FeatureToggleState,
        mapper: ObjectMapper
    ): Consumer<Message<String>> {
        return Consumer { message ->
            try {
                val payload = message.payload
                val headers = message.headers
                logger.debug("Received feature toggle event - Payload: {}, Headers: {}", payload, headers)

                val event = mapper.readValue(payload, FeatureToggleUpdatedEvent::class.java)
                featureToggleState.update(event.parameterName, event.parameterValue)

                logger.info("Updated feature toggle state")
            } catch (e: Exception) {
                logger.error("Error on update feature toggle state", e)
            }
        }
    }

}
