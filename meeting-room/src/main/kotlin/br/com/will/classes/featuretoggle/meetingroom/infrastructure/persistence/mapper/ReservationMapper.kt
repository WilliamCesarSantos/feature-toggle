package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.mapper

    import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity.ReservationJpaEntity
import org.springframework.stereotype.Component

@Component
class ReservationMapper(
    private val loadRoomPort: LoadRoomPort
) {

    fun toEntity(domain: Reservation): ReservationJpaEntity {
        return ReservationJpaEntity(
            id = domain.id,
            roomId = domain.meetingRoom.id!!,
            participants = domain.participants,
            startTime = domain.startTime,
            endTime = domain.endTime,
            requester = domain.requester,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }
    
    fun toDomain(entity: ReservationJpaEntity): Reservation {
        val meetingRoom = loadRoomPort.loadById(entity.roomId)
            ?: throw IllegalStateException("MeetingRoom with id ${entity.roomId} not found")

        return Reservation.reconstitute(
            id = entity.id!!,
            meetingRoom = meetingRoom,
            participants = entity.participants,
            startTime = entity.startTime,
            endTime = entity.endTime,
            requester = entity.requester,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }
}

