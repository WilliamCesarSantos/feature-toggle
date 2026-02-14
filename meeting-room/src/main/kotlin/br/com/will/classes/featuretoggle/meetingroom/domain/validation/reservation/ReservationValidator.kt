package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation

/**
 * Chain of Responsibility handler for reservation validation
 */
abstract class ReservationValidator {

    private var nextValidator: ReservationValidator? = null

    fun setNext(validator: ReservationValidator): ReservationValidator {
        this.nextValidator = validator
        return validator
    }

    fun validate(reservation: Reservation) {
        if (shouldAccept(reservation)) {
            doValidate(reservation)
        }
        nextValidator?.validate(reservation)
    }

    /**
     * Determines if this validator should process the reservation
     * Override to add custom acceptance logic (e.g., feature toggles)
     */
    protected open fun shouldAccept(reservation: Reservation): Boolean = true

    protected abstract fun doValidate(reservation: Reservation)
}

