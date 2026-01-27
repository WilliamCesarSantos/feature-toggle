package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.mapper

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.RoomResponse
import org.springframework.stereotype.Component

@Component
class RoomDtoMapper {

    fun toResponse(domain: MeetingRoom): RoomResponse {
        return RoomResponse(
            id = domain.id!!,
            name = domain.name,
            capacity = domain.capacity,
            location = domain.location,
            description = domain.description,
            createdAt = domain.createdAt
        )
    }
}