package br.com.will.classes.featuretoggle.meetingroom.application.port.output

import br.com.will.classes.featuretoggle.meetingroom.domain.entity.MeetingRoom

interface SaveRoomPort {
    fun save(room: MeetingRoom): MeetingRoom
    fun delete(id: Long)
}