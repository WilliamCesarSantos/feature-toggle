package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class FeatureToggleState {

    private val toggles = ConcurrentHashMap<String, String>()

    fun isEnabled(feature: String): Boolean =
        toggles[feature]?.toBoolean() ?: false

    fun value(feature: String) =
        toggles.getValue(feature)

    fun update(feature: String, value: String) {
        toggles[feature] = value
    }
    
}
