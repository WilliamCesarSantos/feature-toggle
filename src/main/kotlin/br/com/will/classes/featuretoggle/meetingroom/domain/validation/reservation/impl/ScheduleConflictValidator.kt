package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ConflictException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ScheduleConflictValidator(
    private val featureTogglePort: FeatureTogglePort,
    private val loadReservationPort: LoadReservationPort
) : ReservationValidator() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun shouldAccept(reservation: Reservation): Boolean {
        val isEnabled = featureTogglePort.isEnabled("reservation.schedule-conflict-check")
        logger.debug("Schedule conflict validation toggle: enabled={}", isEnabled)
        return isEnabled
    }

    override fun doValidate(reservation: Reservation) {
        val room = reservation.meetingRoom

        val conflicting = loadReservationPort.loadConflictingReservations(
            room.id!!,
            reservation.startTime,
            reservation.endTime
        )

        if (conflicting.isNotEmpty()) {
            logger.warn("Schedule conflict detected: roomId={}, conflictingReservations={}",
                room.id, conflicting.size)
            throw ConflictException("The room is already reserved during the requested time")
        }

        logger.debug("No schedule conflicts found for room: roomId={}", room.id)
    }
}

