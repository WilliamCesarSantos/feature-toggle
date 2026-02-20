package br.com.will.classes.featuretoggle.meetingroom.application.port.input

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom

interface ManageRoomUseCase {
    fun createRoom(
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom

    fun updateRoom(
        id: Long,
        name: String,
        capacity: Int,
        location: String?,
        description: String?
    ): MeetingRoom

    fun deleteRoom(id: Long)

    fun getRoomById(id: Long): MeetingRoom

    fun getAllRooms(): List<MeetingRoom>

    fun findRoomsByMinCapacity(minCapacity: Int): List<MeetingRoom>

    fun findRoomsByLocation(location: String): List<MeetingRoom>

    fun searchRooms(query: String): List<MeetingRoom>
}