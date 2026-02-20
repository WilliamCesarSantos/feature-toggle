package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.slf4j.LoggerFactory
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
@RefreshScope
class FeatureToggleState(
    private val environment: Environment
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val FEATURE_TOGGLE_PREFIX = "feature.toggles."
    }

    fun isEnabled(feature: String): Boolean {
        val propertyName = FEATURE_TOGGLE_PREFIX + feature
        val value = environment.getProperty(propertyName, Boolean::class.java, false)
        logger.debug("Toggle from environment: {}={}", propertyName, value)
        return value
    }

}

