package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Feature Toggle State managed by Spring Environment
 * Automatically refreshed when configuration changes via Spring Cloud Config
 */
@Component
@RefreshScope
class FeatureToggleState(
    private val environment: Environment
) {

    companion object {
        private const val FEATURE_TOGGLE_PREFIX = "feature.toggles."
    }

    /**
     * Check if a feature toggle is enabled
     * @param feature Feature name (e.g., "reservation.capacity-check")
     * @return true if enabled, false otherwise
     */
    fun isEnabled(feature: String): Boolean {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature
        return environment.getProperty(propertyName, Boolean::class.java, false)
    }

    /**
     * Get the value of a feature toggle as String
     * @param feature Feature name
     * @return String value or empty string if not found
     */
    fun value(feature: String): String {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature
        return environment.getProperty(propertyName, "")
    }

    /**
     * Get all feature toggles
     * @return Map of all feature toggles
     */
    fun getAllToggles(): Map<String, String> {
        // This will read all properties that start with feature.toggles.
        val toggles = mutableMapOf<String, String>()

        // Common feature toggle keys
        val knownToggles = listOf(
            "reservation.capacity-check",
            "reservation.schedule-conflict-check"
        )

        knownToggles.forEach { toggle ->
            val propertyName = FEATURE_TOGGLE_PREFIX + toggle
            environment.getProperty(propertyName)?.let { value ->
                toggles[toggle] = value
            }
        }

        return toggles
    }
}
