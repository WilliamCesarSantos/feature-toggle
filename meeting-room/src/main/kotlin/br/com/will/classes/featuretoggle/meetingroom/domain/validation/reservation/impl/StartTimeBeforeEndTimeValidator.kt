package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

@Component
class StartTimeBeforeEndTimeValidator : ReservationValidator() {

    override fun doValidate(reservation: Reservation) {
        if (reservation.startTime.isAfter(reservation.endTime) ||
            reservation.startTime.isEqual(reservation.endTime)) {
            throw InvalidReservationException(
                "Start time (${reservation.startTime}) must be before end time (${reservation.endTime})"
            )
        }
    }
}

