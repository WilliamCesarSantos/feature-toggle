package br.com.will.classes.featuretoggle.meetingroom.application.service

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageReservationUseCase
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ResourceNotFoundException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl.ReservationValidatorChain
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReservationService(
    private val loadRoomPort: LoadRoomPort,
    private val loadReservationPort: LoadReservationPort,
    private val saveReservationPort: SaveReservationPort,
    private val validatorChain: ReservationValidatorChain
) : ManageReservationUseCase {

    @Transactional
    override fun createReservation(
        roomId: Long,
        participants: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        requester: String,
        notes: String?
    ): Reservation {
        val room = loadRoomPort.loadById(roomId)
            ?: throw ResourceNotFoundException("MeetingRoom", roomId)

        val reservation = Reservation.create(
            meetingRoom = room,
            participants = participants,
            startTime = startTime,
            endTime = endTime,
            requester = requester,
            notes = notes
        ).validate(validatorChain.buildChain())

        return saveReservationPort.save(reservation)
    }

    @Transactional
    override fun cancelReservation(id: Long) {
        if (loadReservationPort.loadById(id) == null) {
            throw ResourceNotFoundException("Reservation", id)
        }
        
        saveReservationPort.delete(id)
    }

    @Transactional(readOnly = true)
    override fun getReservationById(id: Long): Reservation {
        return loadReservationPort.loadById(id)
            ?: throw ResourceNotFoundException("Reservation", id)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByRoom(roomId: Long): List<Reservation> {
        if (loadRoomPort.loadById(roomId) == null) {
            throw ResourceNotFoundException("MeetingRoom", roomId)
        }
        
        return loadReservationPort.loadByRoomId(roomId)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByRequester(requester: String): List<Reservation> {
        return loadReservationPort.loadByRequester(requester)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByTimeRange(
        roomId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation> {
        if (loadRoomPort.loadById(roomId) == null) {
            throw ResourceNotFoundException("MeetingRoom", roomId)
        }
        
        return loadReservationPort.loadConflictingReservations(roomId, startTime, endTime)
    }
}
