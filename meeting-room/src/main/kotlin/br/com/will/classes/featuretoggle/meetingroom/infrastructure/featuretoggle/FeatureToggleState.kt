package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.slf4j.LoggerFactory
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.context.environment.EnvironmentChangeEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
@RefreshScope
class FeatureToggleState(
    private val environment: Environment
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val toggleOverrides = ConcurrentHashMap<String, String>()

    companion object {
        private const val FEATURE_TOGGLE_PREFIX = "feature.toggles."
    }

    fun isEnabled(feature: String): Boolean {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature

        toggleOverrides[propertyName]?.let {
            logger.debug("Toggle from override: {}={}", propertyName, it)
            return it.toBoolean()
        }

        val value = environment.getProperty(propertyName, Boolean::class.java, false)
        logger.debug("Toggle from environment: {}={}", propertyName, value)
        return value
    }

    fun update(feature: String, newValue: String) {
        toggleOverrides[feature] = newValue
        logger.info("Toggle override updated: {}={}", feature, newValue)
    }

    @EventListener
    fun handleEnvironmentChange(event: EnvironmentChangeEvent) {
        val clearedCount = toggleOverrides.size
        toggleOverrides.clear()
        logger.info("Environment changed: cleared {} toggle overrides", clearedCount)
    }

}
