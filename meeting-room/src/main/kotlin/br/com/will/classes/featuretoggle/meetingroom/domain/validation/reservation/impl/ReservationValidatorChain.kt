package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

@Component
class ReservationValidatorChain(
    private val startTimeBeforeEndTimeValidator: StartTimeBeforeEndTimeValidator,
    private val minimumParticipantsValidator: MinimumParticipantsValidator,
    private val requesterNotBlankValidator: RequesterNotBlankValidator,
    private val capacityValidator: CapacityValidator,
    private val scheduleConflictValidator: ScheduleConflictValidator
) {

    fun buildChain(): ReservationValidator {
        startTimeBeforeEndTimeValidator
            .then(minimumParticipantsValidator)
            .then(requesterNotBlankValidator)
            .then(capacityValidator)
            .then(scheduleConflictValidator)

        return startTimeBeforeEndTimeValidator
    }
}

