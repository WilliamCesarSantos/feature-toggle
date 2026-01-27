package br.com.will.classes.featuretoggle.togglestream.event

import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

@Service
class FeatureTogglePublisher(
    private val streamBridge: StreamBridge
) {

    fun publishToggle(feature: String, enabled: Boolean) {
        streamBridge.send(
            "toggle-out",
            FeatureToggleEventDto(feature, enabled)
        )
    }
}

data class FeatureToggleEventDto(
    val feature: String,
    val enabled: Boolean
)
