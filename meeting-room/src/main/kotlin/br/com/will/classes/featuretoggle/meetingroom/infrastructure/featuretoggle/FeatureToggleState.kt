package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class FeatureToggleState {

    private val toggles = ConcurrentHashMap<String, Boolean>()

    fun isEnabled(feature: String): Boolean =
        toggles.getOrDefault(feature, false)

    fun update(feature: String, enabled: Boolean) {
        toggles[feature] = enabled
    }
}
