package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
@RefreshScope
class FeatureToggleState(
    private val environment: Environment
) {

    companion object {
        private const val FEATURE_TOGGLE_PREFIX = "feature.toggles."
    }

    fun isEnabled(feature: String): Boolean {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature
        return environment.getProperty(propertyName, Boolean::class.java, false)
    }

    fun update(feature: String, newValue: String) {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature
        environment.getProperty(propertyName, newValue)
    }

}
