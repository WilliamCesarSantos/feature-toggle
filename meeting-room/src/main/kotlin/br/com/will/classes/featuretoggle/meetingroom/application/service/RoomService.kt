package br.com.will.classes.featuretoggle.meetingroom.application.service

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageRoomUseCase
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.FeatureTogglePort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveRoomPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ConflictException
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService(
    private val loadRoomPort: LoadRoomPort,
    private val saveRoomPort: SaveRoomPort
) : ManageRoomUseCase {

    @Transactional
    override fun createRoom(
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom {
        loadRoomPort.loadByName(name)?.let {
            throw ConflictException("A room with name '$name' already exists")
        }

        val room = MeetingRoom.create(
            name = name,
            capacity = capacity,
            location = location,
            description = description
        )

        return saveRoomPort.save(room)
    }

    @Transactional
    override fun updateRoom(
        id: Long,
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom {
        val existingRoom = loadRoomPort.loadById(id)
            ?: throw ResourceNotFoundException("MeetingRoom", id)

        if (name != existingRoom.name) {
            loadRoomPort.loadByName(name)?.let {
                throw ConflictException("A room with name '$name' already exists")
            }
        }

        val updatedRoom = existingRoom.update(
            name = name,
            capacity = capacity,
            location = location,
            description = description
        )

        return saveRoomPort.save(updatedRoom)
    }

    @Transactional
    override fun deleteRoom(id: Long) {
        if (loadRoomPort.loadById(id) == null) {
            throw ResourceNotFoundException("MeetingRoom", id)
        }
        
        saveRoomPort.delete(id)
    }

    @Transactional(readOnly = true)
    override fun getRoomById(id: Long): MeetingRoom {
        return loadRoomPort.loadById(id)
            ?: throw ResourceNotFoundException("MeetingRoom", id)
    }

    @Transactional(readOnly = true)
    override fun getAllRooms(): List<MeetingRoom> {
        return loadRoomPort.loadAll()
    }

    @Transactional(readOnly = true)
    override fun findRoomsByMinCapacity(minCapacity: Int): List<MeetingRoom> {
        return loadRoomPort.loadByMinCapacity(minCapacity)
    }

    @Transactional(readOnly = true)
    override fun findRoomsByLocation(location: String): List<MeetingRoom> {
        return loadRoomPort.loadByLocation(location)
    }

    @Transactional(readOnly = true)
    override fun searchRooms(query: String): List<MeetingRoom> {
        return loadRoomPort.loadByNameOrDescription(query)
    }
}