package br.com.will.classes.featuretoggle.meetingroom.application.port.output

interface FeatureTogglePort {

    fun isEnabled(featureName: String): Boolean

}