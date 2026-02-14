package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

/**
 * Validates that participants count is at least 1
 */
@Component
class MinimumParticipantsValidator : ReservationValidator() {

    override fun doValidate(reservation: Reservation) {
        if (reservation.participants < 1) {
            throw InvalidReservationException(
                "Number of participants must be at least 1, but was ${reservation.participants}"
            )
        }
    }
}

