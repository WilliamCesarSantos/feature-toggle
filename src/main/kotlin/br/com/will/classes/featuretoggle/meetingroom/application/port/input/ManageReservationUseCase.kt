package br.com.will.classes.featuretoggle.meetingroom.application.port.input

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import java.time.LocalDateTime

interface ManageReservationUseCase {
    fun createReservation(
        roomId: Long,
        participants: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        requester: String,
        notes: String?
    ): Reservation
    
    fun cancelReservation(id: Long)
    fun getReservationById(id: Long): Reservation
    fun findReservationsByRoom(roomId: Long): List<Reservation>
    fun findReservationsByRequester(requester: String): List<Reservation>
    fun findReservationsByTimeRange(
        roomId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation>
}