package br.com.will.classes.featuretoggle.meetingroom.application.service

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageRoomUseCase
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.LoadRoomPort
import br.com.will.classes.featuretoggle.meetingroom.application.port.output.SaveRoomPort
import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ConflictException
import br.com.will.classes.featuretoggle.meetingroom.domain.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService(
    private val loadRoomPort: LoadRoomPort,
    private val saveRoomPort: SaveRoomPort
) : ManageRoomUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun createRoom(
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom {
        logger.info("Creating room: name={}, capacity={}", name, capacity)

        loadRoomPort.loadByName(name)?.let {
            throw ConflictException("A room with name '$name' already exists")
        }

        val room = MeetingRoom.create(
            name = name,
            capacity = capacity,
            location = location,
            description = description
        )

        val saved = saveRoomPort.save(room)
        logger.info("Room created successfully: id={}", saved.id)
        return saved
    }

    @Transactional
    override fun updateRoom(
        id: Long,
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom {
        logger.info("Updating room: id={}, name={}", id, name)

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

        val saved = saveRoomPort.save(updatedRoom)
        logger.info("Room updated successfully: id={}", id)
        return saved
    }

    @Transactional
    override fun deleteRoom(id: Long) {
        logger.info("Deleting room: id={}", id)

        if (loadRoomPort.loadById(id) == null) {
            throw ResourceNotFoundException("MeetingRoom", id)
        }
        
        saveRoomPort.delete(id)
        logger.info("Room deleted: id={}", id)
    }

    @Transactional(readOnly = true)
    override fun getRoomById(id: Long): MeetingRoom {
        logger.debug("Fetching room: id={}", id)
        return loadRoomPort.loadById(id)
            ?: throw ResourceNotFoundException("MeetingRoom", id)
    }

    @Transactional(readOnly = true)
    override fun getAllRooms(): List<MeetingRoom> {
        logger.debug("Fetching all rooms")
        return loadRoomPort.loadAll()
    }

    @Transactional(readOnly = true)
    override fun findRoomsByMinCapacity(minCapacity: Int): List<MeetingRoom> {
        logger.debug("Searching rooms with min capacity: {}", minCapacity)
        return loadRoomPort.loadByMinCapacity(minCapacity)
    }

    @Transactional(readOnly = true)
    override fun findRoomsByLocation(location: String): List<MeetingRoom> {
        logger.debug("Searching rooms at location: {}", location)
        return loadRoomPort.loadByLocation(location)
    }

    @Transactional(readOnly = true)
    override fun searchRooms(query: String): List<MeetingRoom> {
        logger.debug("Searching rooms with query: {}", query)
        return loadRoomPort.loadByNameOrDescription(query)
    }
}