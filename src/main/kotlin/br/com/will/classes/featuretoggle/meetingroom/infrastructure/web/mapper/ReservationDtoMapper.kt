package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.mapper

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.Reservation
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.ReservationResponse
import org.springframework.stereotype.Component

@Component
class ReservationDtoMapper {

    fun toResponse(domain: Reservation): ReservationResponse {
        return ReservationResponse(
            id = domain.id!!,
            roomId = domain.meetingRoom.id!!,
            participants = domain.participants,
            startTime = domain.startTime,
            endTime = domain.endTime,
            requester = domain.requester,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }
}