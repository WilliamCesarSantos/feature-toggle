package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.adapter

import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveRoomPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.mapper.MeetingRoomMapper
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.repository.MeetingRoomJpaRepository
import org.springframework.stereotype.Component

@Component
class RoomPersistenceAdapter(
    private val repository: MeetingRoomJpaRepository,
    private val mapper: MeetingRoomMapper
) : LoadRoomPort, SaveRoomPort {

    override fun loadById(id: Long): MeetingRoom? {
        return repository.findById(id).map(mapper::toDomain).orElse(null)
    }

    override fun loadByName(name: String): MeetingRoom? {
        return repository.findByName(name)?.let(mapper::toDomain)
    }

    override fun loadAll(): List<MeetingRoom> {
        return repository.findAll().map(mapper::toDomain)
    }

    override fun loadByMinCapacity(minCapacity: Int): List<MeetingRoom> {
        return repository.findByCapacityGreaterThanEqual(minCapacity).map(mapper::toDomain)
    }

    override fun loadByLocation(location: String): List<MeetingRoom> {
        return repository.findByLocationContainingIgnoreCase(location).map(mapper::toDomain)
    }

    override fun loadByNameOrDescription(query: String): List<MeetingRoom> {
        return repository.findByNameOrDescriptionContaining(query).map(mapper::toDomain)
    }

    override fun save(room: MeetingRoom): MeetingRoom {
        val entity = mapper.toEntity(room)
        val savedEntity = repository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }
}