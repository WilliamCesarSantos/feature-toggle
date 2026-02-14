package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ConflictException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

/**
 * Validator for schedule conflicts (Feature Toggle controlled)
 */
@Component
class ScheduleConflictValidator(
    private val featureTogglePort: FeatureTogglePort,
    private val loadReservationPort: LoadReservationPort
) : ReservationValidator() {

    override fun shouldAccept(reservation: Reservation): Boolean {
        return featureTogglePort.isEnabled("reservation.schedule-conflict-check")
    }

    override fun doValidate(reservation: Reservation) {
        val room = reservation.meetingRoom

        val conflicting = loadReservationPort.loadConflictingReservations(
            room.id!!,
            reservation.startTime,
            reservation.endTime
        )

        if (conflicting.isNotEmpty()) {
            throw ConflictException("The room is already reserved during the requested time")
        }
    }
}

