package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto

import java.time.LocalDateTime

data class ReservationResponse(
    val id: Long,
    val roomId: Long,
    val participants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val requester: String,
    val notes: String?,
    val createdAt: LocalDateTime
)

data class CreateReservationRequest(
    val roomId: Long,
    val participants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val requester: String,
    val notes: String? = null
)