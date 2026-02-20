package br.com.will.classes.featuretoggle.meetingroom.infrastructure.message

import java.time.LocalDateTime

data class FeatureToggleUpdatedEvent(
    val source: String?,
    val createdAt: LocalDateTime,
    val originService: String,
    val parameterName: String,
    val parameterValue: String,
    val parameterType: String
)
