package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.controller

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageRoomUseCase
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.CreateRoomRequest
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.RoomResponse
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.UpdateRoomRequest
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.mapper.RoomDtoMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val manageRoomUseCase: ManageRoomUseCase,
    private val roomDtoMapper: RoomDtoMapper
) {

    @GetMapping
    fun getAllRooms(): ResponseEntity<List<RoomResponse>> {
        val rooms = manageRoomUseCase.getAllRooms()
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/{id}")
    fun getRoomById(@PathVariable id: Long): ResponseEntity<RoomResponse> {
        val room = manageRoomUseCase.getRoomById(id)
        return ResponseEntity.ok(roomDtoMapper.toResponse(room))
    }
    
    @GetMapping("/search/capacity")
    fun findRoomsByCapacity(@RequestParam minCapacity: Int): ResponseEntity<List<RoomResponse>> {
        val rooms = manageRoomUseCase.findRoomsByMinCapacity(minCapacity)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/search/location")
    fun findRoomsByLocation(@RequestParam location: String): ResponseEntity<List<RoomResponse>> {
        val rooms = manageRoomUseCase.findRoomsByLocation(location)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/search")
    fun searchRooms(@RequestParam query: String): ResponseEntity<List<RoomResponse>> {
        val rooms = manageRoomUseCase.searchRooms(query)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @PostMapping
    fun createRoom(@RequestBody request: CreateRoomRequest): ResponseEntity<RoomResponse> {
        val room = manageRoomUseCase.createRoom(
            name = request.name,
            capacity = request.capacity,
            location = request.location,
            description = request.description
        )
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(roomDtoMapper.toResponse(room))
    }
    
    @PutMapping("/{id}")
    fun updateRoom(
        @PathVariable id: Long,
        @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<RoomResponse> {
        val room = manageRoomUseCase.updateRoom(
            id = id,
            name = request.name,
            capacity = request.capacity,
            location = request.location,
            description = request.description
        )
        return ResponseEntity.ok(roomDtoMapper.toResponse(room))
    }
    
    @DeleteMapping("/{id}")
    fun deleteRoom(@PathVariable id: Long): ResponseEntity<Void> {
        manageRoomUseCase.deleteRoom(id)
        return ResponseEntity.noContent().build()
    }
}