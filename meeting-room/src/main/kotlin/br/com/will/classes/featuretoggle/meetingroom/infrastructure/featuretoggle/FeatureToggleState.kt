package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.slf4j.LoggerFactory
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.context.environment.EnvironmentChangeEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
@RefreshScope
class FeatureToggleState(
    private val environment: Environment
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val toggleOverrides = ConcurrentHashMap<String, ToggleValue>()

    companion object {
        private const val FEATURE_TOGGLE_PREFIX = "feature.toggles."
    }

    fun isEnabled(feature: String): Boolean {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature

        toggleOverrides[propertyName]?.let {
            logger.debug("Toggle from override: {}={}", propertyName, it.value)
            return it.value.toBoolean()
        }

        val value = environment.getProperty(propertyName, Boolean::class.java, false)
        logger.debug("Toggle from environment: {}={}", propertyName, value)
        return value
    }

    fun update(feature: String, newValue: String, updatedAt: LocalDateTime) {
        val current = toggleOverrides[feature]

        if (current != null && current.updatedAt.isAfter(updatedAt)) {
            logger.warn("Ignoring outdated toggle update: {}={} (current: {}, new: {})",
                feature, newValue, current.updatedAt, updatedAt)
            return
        }

        toggleOverrides[feature] = ToggleValue(newValue, updatedAt)
        logger.info("Toggle override updated: {}={} at timestamp={}", feature, newValue, updatedAt)
    }

    @EventListener
    fun handleEnvironmentChange(event: EnvironmentChangeEvent) {
        val clearedCount = toggleOverrides.size
        toggleOverrides.clear()
        logger.info("Environment changed: cleared {} toggle overrides", clearedCount)
    }

}

data class ToggleValue(
    val value: String,
    val updatedAt: LocalDateTime
)

