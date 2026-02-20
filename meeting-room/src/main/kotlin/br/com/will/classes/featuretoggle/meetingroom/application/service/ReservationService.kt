package br.com.will.classes.featuretoggle.meetingroom.application.service

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageReservationUseCase
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ResourceNotFoundException
import br.com.will.classes.featuretoggle.meetingroom.domain.validation.reservation.impl.ReservationValidatorChain
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun createReservation(
        roomId: Long,
        participants: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        requester: String,
        notes: String?
    ): Reservation {
        logger.info("Creating reservation: roomId={}, participants={}, requester={}", roomId, participants, requester)

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

        val saved = saveReservationPort.save(reservation)
        logger.info("Reservation created successfully: id={}", saved.id)

        return saved
    }

    @Transactional
    override fun cancelReservation(id: Long) {
        logger.info("Canceling reservation: id={}", id)

        if (loadReservationPort.loadById(id) == null) {
            throw ResourceNotFoundException("Reservation", id)
        }
        
        saveReservationPort.delete(id)
        logger.info("Reservation canceled: id={}", id)
    }

    @Transactional(readOnly = true)
    override fun getReservationById(id: Long): Reservation {
        logger.debug("Fetching reservation: id={}", id)
        return loadReservationPort.loadById(id)
            ?: throw ResourceNotFoundException("Reservation", id)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByRoom(roomId: Long): List<Reservation> {
        logger.debug("Fetching reservations for room: roomId={}", roomId)

        if (loadRoomPort.loadById(roomId) == null) {
            throw ResourceNotFoundException("MeetingRoom", roomId)
        }
        
        return loadReservationPort.loadByRoomId(roomId)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByRequester(requester: String): List<Reservation> {
        logger.debug("Fetching reservations for requester: {}", requester)
        return loadReservationPort.loadByRequester(requester)
    }

    @Transactional(readOnly = true)
    override fun findReservationsByTimeRange(
        roomId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation> {
        logger.debug("Fetching reservations for room: roomId={}, time range: {} to {}", roomId, startTime, endTime)

        if (loadRoomPort.loadById(roomId) == null) {
            throw ResourceNotFoundException("MeetingRoom", roomId)
        }
        
        return loadReservationPort.loadConflictingReservations(roomId, startTime, endTime)
    }
}
