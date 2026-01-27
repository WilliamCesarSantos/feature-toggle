package br.com.will.classes.featuretoggle.meetingroom.application.port.output

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import java.time.LocalDateTime

interface LoadReservationPort {
    fun loadById(id: Long): Reservation?
    fun loadByRoomId(roomId: Long): List<Reservation>
    fun loadByRequester(requester: String): List<Reservation>
    fun loadConflictingReservations(
        roomId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation>
}