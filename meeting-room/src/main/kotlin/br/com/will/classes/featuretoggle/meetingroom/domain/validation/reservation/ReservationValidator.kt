package br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation

abstract class ReservationValidator {

    private var nextValidator: ReservationValidator? = null

    fun then(validator: ReservationValidator): ReservationValidator {
        this.nextValidator = validator
        return validator
    }

    fun validate(reservation: Reservation) {
        if (shouldAccept(reservation)) {
            doValidate(reservation)
        }
        nextValidator?.validate(reservation)
    }

    protected open fun shouldAccept(reservation: Reservation): Boolean = true

    protected abstract fun doValidate(reservation: Reservation)
}

