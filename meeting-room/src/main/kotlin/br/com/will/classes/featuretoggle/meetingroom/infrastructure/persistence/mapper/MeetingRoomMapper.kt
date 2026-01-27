package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.mapper

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity.MeetingRoomJpaEntity
import org.springframework.stereotype.Component

@Component
class MeetingRoomMapper {
    fun toEntity(domain: MeetingRoom): MeetingRoomJpaEntity {
        return MeetingRoomJpaEntity(
            id = domain.id,
            name = domain.name,
            capacity = domain.capacity,
            location = domain.location,
            description = domain.description,
            createdAt = domain.createdAt
        )
    }

    fun toDomain(entity: MeetingRoomJpaEntity): MeetingRoom {
        return MeetingRoom.reconstitute(
            id = entity.id!!,
            name = entity.name,
            capacity = entity.capacity,
            location = entity.location,
            description = entity.description,
            createdAt = entity.createdAt
        )
    }
}

