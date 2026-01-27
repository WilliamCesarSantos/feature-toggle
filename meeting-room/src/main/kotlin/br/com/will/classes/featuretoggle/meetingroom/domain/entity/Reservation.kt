package br.com.will.classes.featuretoggle.meetingroom.domain.entity

import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import java.time.LocalDateTime

class Reservation private constructor(
    val id: Long?,
    val roomId: Long,
    val participants: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val requester: String,
    val notes: String?,
    val createdAt: LocalDateTime
) {
    init {
        validate()
    }

    private fun validate() {
        if (startTime.isAfter(endTime)) {
            throw InvalidReservationException("Start time must be before end time")
        }
        
        if (participants < 1) {
            throw InvalidReservationException("Number of participants must be at least 1")
        }
        
        if (requester.isBlank()) {
            throw InvalidReservationException("Requester name cannot be empty")
        }
    }
    
    companion object {
        fun create(
            roomId: Long,
            participants: Int,
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            requester: String,
            notes: String? = null
        ): Reservation {
            return Reservation(
                id = null,
                roomId = roomId,
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
            roomId: Long,
            participants: Int,
            startTime: LocalDateTime,
            endTime: LocalDateTime,
            requester: String,
            notes: String?,
            createdAt: LocalDateTime
        ): Reservation {
            return Reservation(
                id = id,
                roomId = roomId,
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