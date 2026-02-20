package br.com.will.classes.featuretoggle.meetingroom.domain.entity

import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import java.time.LocalDateTime

class Reservation private constructor(
    val id: Long?,
    val meetingRoom: MeetingRoom,
    val participants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val requester: String,
    val notes: String?,
    val createdAt: LocalDateTime
) {

    fun validate(validator: ReservationValidator): Reservation {
        validator.validate(this)
        return this
    }

    companion object {
        fun create(
            meetingRoom: MeetingRoom,
            participants: Int,
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            requester: String,
            notes: String? = null
        ): Reservation {
            return Reservation(
                id = null,
                meetingRoom = meetingRoom,
                participants = participants,
                startTime = startTime,
                endTime = endTime,
                requester = requester,
                notes = notes,
                createdAt = LocalDateTime.now()
            )
        }
        
        fun reconstitute(
            id: Long,
            meetingRoom: MeetingRoom,
            participants: Int,
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            requester: String,
            notes: String?,
            createdAt: LocalDateTime
        ): Reservation {
            return Reservation(
                id = id,
                meetingRoom = meetingRoom,
                participants = participants,
                startTime = startTime,
                endTime = endTime,
                requester = requester,
                notes = notes,
                createdAt = createdAt
            )
        }
    }
}

