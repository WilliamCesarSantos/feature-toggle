package br.com.will.classes.featuretoggle.meetingroom.application.service

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageReservationUseCase
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ConflictException
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.InvalidReservationException
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReservationService(
    private val loadRoomPort: LoadRoomPort,
    private val loadReservationPort: LoadReservationPort,
    private val saveReservationPort: SaveReservationPort,
    private val featureTogglePort: FeatureTogglePort
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

        if (featureTogglePort.isEnabled("capacity-check") && participants > room.capacity) {
            throw InvalidReservationException(
                "Requested participants ($participants) exceeds room capacity (${room.capacity})"
            )
        }

        if (featureTogglePort.isEnabled("schedule-conflict-check")) {
            val conflicting = loadReservationPort.loadConflictingReservations(
                roomId, startTime, endTime
            )
            
            if (conflicting.isNotEmpty()) {
                throw ConflictException("The room is already reserved during the requested time")
            }
        }
        
        val reservation = Reservation.create(
            roomId = roomId,
            participants = participants,
            startTime = startTime,
            endTime = endTime,
            requester = requester,
            notes = notes
        )
        
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