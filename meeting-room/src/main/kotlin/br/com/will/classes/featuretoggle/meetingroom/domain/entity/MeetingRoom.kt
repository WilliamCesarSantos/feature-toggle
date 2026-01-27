package br.com.will.classes.featuretoggle.meetingroom.domain.entity

import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidRoomException
import java.time.LocalDateTime

class MeetingRoom private constructor(
    val id: Long?,
    val name: String,
    val capacity: Int,
    val location: String?,
    val description: String?,
    val createdAt: LocalDateTime
) {
    init {
        validate()
    }

    private fun validate() {
        if (name.isBlank()) {
            throw InvalidRoomException("Room name cannot be empty")
        }

        if (capacity < 1) {
            throw InvalidRoomException("Room capacity must be at least 1")
        }
    }

    fun update(
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom {
        return MeetingRoom(
            id = this.id,
            name = name,
            capacity = capacity,
            location = location,
            description = description,
            createdAt = this.createdAt
        )
    }

    companion object {
        fun create(
            name: String,
            capacity: Int,
            location: String? = null,
            description: String? = null
        ): MeetingRoom {
            return MeetingRoom(
                id = null,
                name = name,
                capacity = capacity,
                location = location,
                description = description,
                createdAt = LocalDateTime.now()
            )
        }

        fun reconstitute(
            id: Long,
            name: String,
            capacity: Int,
            location: String?,
            description: String?,
            createdAt: LocalDateTime
        ): MeetingRoom {
            return MeetingRoom(
                id = id,
                name = name,
                capacity = capacity,
                location = location,
                description = description,
                createdAt = createdAt
            )
        }
    }
}

