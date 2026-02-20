package br.com.will.classes.featuretoggle.meetingroom.application.port.output

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom

interface LoadRoomPort {
    fun loadById(id: Long): MeetingRoom?
    fun loadByName(name: String): MeetingRoom?
    fun loadAll(): List<MeetingRoom>
    fun loadByMinCapacity(minCapacity: Int): List<MeetingRoom>
    fun loadByLocation(location: String): List<MeetingRoom>
    fun loadByNameOrDescription(query: String): List<MeetingRoom>
}