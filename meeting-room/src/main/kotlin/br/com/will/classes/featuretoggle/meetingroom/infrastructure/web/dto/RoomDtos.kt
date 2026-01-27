package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto

import java.time.LocalDateTime

data class RoomResponse(
    val id: Long,
    val name: String,
    val capacity: Int,
    val location: String?,
    val description: String?,
    val createdAt: LocalDateTime
)

data class CreateRoomRequest(
    val name: String,
    val capacity: Int,
    val location: String?,
    val description: String?
)

data class UpdateRoomRequest(
    val name: String,
    val capacity: Int,
    val location: String?,
    val description: String?
)

