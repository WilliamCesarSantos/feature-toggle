package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl

import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.ReservationValidator
import org.springframework.stereotype.Component

/**
 * Builds the chain of validators using Chain of Responsibility pattern
 */
@Component
class ReservationValidatorChain(
    private val startTimeBeforeEndTimeValidator: StartTimeBeforeEndTimeValidator,
    private val minimumParticipantsValidator: MinimumParticipantsValidator,
    private val requesterNotBlankValidator: RequesterNotBlankValidator,
    private val capacityValidator: CapacityValidator,
    private val scheduleConflictValidator: ScheduleConflictValidator
) {

    /**
     * Builds and returns the chain of validators
     */
    fun buildChain(): ReservationValidator {
        // Build chain: StartTime -> MinParticipants -> RequesterNotBlank -> Capacity -> ScheduleConflict
        startTimeBeforeEndTimeValidator
            .setNext(minimumParticipantsValidator)
            .setNext(requesterNotBlankValidator)
            .setNext(capacityValidator)
            .setNext(scheduleConflictValidator)

        return startTimeBeforeEndTimeValidator
    }
}

