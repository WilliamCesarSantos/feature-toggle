package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle.FeatureToggleState
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class FeatureToggleEventConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean("toggleUpdatedConsumer")
    fun toggleUpdatedConsumer(
        featureToggleState: FeatureToggleState
    ): Consumer<FeatureToggleUpdatedEvent> {
        return Consumer { event ->
            try {
                logger.debug("Received toggle event: {}", event)

                featureToggleState.update(
                    event.parameterName,
                    event.parameterValue,
                    event.createdAt
                )

                logger.info("Toggle updated: parameter={}, value={}, timestamp={}",
                    event.parameterName, event.parameterValue, event.createdAt)
            } catch (e: Exception) {
                logger.error("Error updating toggle state", e)
            }
        }
    }

}
