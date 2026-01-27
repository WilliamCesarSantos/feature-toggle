package br.com.will.classes.featuretoggle.meetingroom.infrastructure.featuretoggle

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import org.springframework.stereotype.Component

@Component
class FeatureToggleAdapter(
    private val toggleState: FeatureToggleState
) : FeatureTogglePort {
    
    override fun isEnabled(featureName: String): Boolean {
        return toggleState.isEnabled(featureName)
    }
}