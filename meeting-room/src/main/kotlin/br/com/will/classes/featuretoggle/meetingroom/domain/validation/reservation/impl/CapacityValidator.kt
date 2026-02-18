package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CapacityValidator(
    private val featureTogglePort: FeatureTogglePort
) : ReservationValidator() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun shouldAccept(reservation: Reservation): Boolean {
        val isEnabled = featureTogglePort.isEnabled("reservation.capacity-check")
        logger.debug("Capacity validation toggle: enabled={}", isEnabled)
        return isEnabled
    }

    override fun doValidate(reservation: Reservation) {
        val room = reservation.meetingRoom

        if (reservation.participants > room.capacity) {
            logger.warn("Capacity exceeded: requested={}, capacity={}, room={}",
                reservation.participants, room.capacity, room.name)
            throw InvalidReservationException(
                "Requested participants (${reservation.participants}) exceeds room capacity (${room.capacity})"
            )
        }

        logger.debug("Capacity validation passed: participants={}, capacity={}",
            reservation.participants, room.capacity)
    }
}

