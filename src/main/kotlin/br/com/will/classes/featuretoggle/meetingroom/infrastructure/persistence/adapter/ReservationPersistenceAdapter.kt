package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.adapter

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadReservationPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveReservationPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.mapper.ReservationMapper
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.repository.ReservationJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReservationPersistenceAdapter(
    private val repository: ReservationJpaRepository,
    private val mapper: ReservationMapper
) : LoadReservationPort, SaveReservationPort {

    override fun loadById(id: Long): Reservation? {
        return repository.findById(id).map(mapper::toDomain).orElse(null)
    }

    override fun loadByRoomId(roomId: Long): List<Reservation> {
        return repository.findByRoomId(roomId).map(mapper::toDomain)
    }

    override fun loadByRequester(requester: String): List<Reservation> {
        return repository.findByRequesterContainingIgnoreCase(requester).map(mapper::toDomain)
    }

    override fun loadConflictingReservations(
        roomId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<Reservation> {
        return repository.findConflictingReservations(roomId, startTime, endTime).map(mapper::toDomain)
    }

    override fun save(reservation: Reservation): Reservation {
        val entity = mapper.toEntity(reservation)
        val savedEntity = repository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }
}