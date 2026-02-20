package br.com.will.classes.featuretoggle.meetingroom.application.port.output

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation

interface SaveReservationPort {
    fun save(reservation: Reservation): Reservation
    fun delete(id: Long)
}