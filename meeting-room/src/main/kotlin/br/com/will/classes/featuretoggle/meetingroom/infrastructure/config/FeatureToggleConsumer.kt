package br.com.will.classes.featuretoggle.meetingroom.infrastructure.config

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

@Configuration
class FeatureToggleConsumer(
    private val state: FeatureToggleState,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun toggleListener(): Consumer<String> =
        Consumer { message ->
            try {
                val event = objectMapper.readValue(message, FeatureToggleEventDto::class.java)
                state.update(event.feature, event.enabled)
                logger.info("Feature '${event.feature}' updated to '${event.enabled}'")
            } catch (e: Exception) {
                logger.error("Error processing feature toggle event: $message", e)
            }
        }
}


data class FeatureToggleEventDto(
    val feature: String,
    val enabled: Boolean
)
