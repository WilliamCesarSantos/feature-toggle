package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

data class FeatureToggleUpdatedEvent(
    val source: String,
    val origin: String,
    val parameterName: String,
    val parameterValue: String,
    val parameterType: String
)
