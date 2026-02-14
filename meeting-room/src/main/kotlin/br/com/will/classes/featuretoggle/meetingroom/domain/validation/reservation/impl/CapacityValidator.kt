package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

/**
 * Validator for room capacity check (Feature Toggle controlled)
 */
@Component
class CapacityValidator(
    private val featureTogglePort: FeatureTogglePort
) : ReservationValidator() {

    override fun shouldAccept(reservation: Reservation): Boolean {
        return featureTogglePort.isEnabled("reservation.capacity-check")
    }

    override fun doValidate(reservation: Reservation) {
        val room = reservation.meetingRoom

        if (reservation.participants > room.capacity) {
            throw InvalidReservationException(
                "Requested participants (${reservation.participants}) exceeds room capacity (${room.capacity})"
            )
        }
    }
}

