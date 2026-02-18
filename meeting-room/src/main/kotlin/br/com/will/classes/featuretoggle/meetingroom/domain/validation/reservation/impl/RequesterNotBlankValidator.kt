package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

@Component
class RequesterNotBlankValidator : ReservationValidator() {

    override fun doValidate(reservation: Reservation) {
        if (reservation.requester.isBlank()) {
            throw InvalidReservationException("Requester name cannot be empty or blank")
        }
    }
}

