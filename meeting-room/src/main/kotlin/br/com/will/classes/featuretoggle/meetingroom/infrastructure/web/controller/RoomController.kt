package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.controller

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageRoomUseCase
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.CreateRoomRequest
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.RoomResponse
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.UpdateRoomRequest
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.mapper.RoomDtoMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val manageRoomUseCase: ManageRoomUseCase,
    private val roomDtoMapper: RoomDtoMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun getAllRooms(): ResponseEntity<List<RoomResponse>> {
        logger.debug("GET /api/rooms - Listing all rooms")
        val rooms = manageRoomUseCase.getAllRooms()
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/{id}")
    fun getRoomById(@PathVariable id: Long): ResponseEntity<RoomResponse> {
        logger.debug("GET /api/rooms/{} - Fetching room", id)
        val room = manageRoomUseCase.getRoomById(id)
        return ResponseEntity.ok(roomDtoMapper.toResponse(room))
    }
    
    @GetMapping("/search/capacity")
    fun findRoomsByCapacity(@RequestParam minCapacity: Int): ResponseEntity<List<RoomResponse>> {
        logger.debug("GET /api/rooms/search/capacity?minCapacity={}", minCapacity)
        val rooms = manageRoomUseCase.findRoomsByMinCapacity(minCapacity)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/search/location")
    fun findRoomsByLocation(@RequestParam location: String): ResponseEntity<List<RoomResponse>> {
        logger.debug("GET /api/rooms/search/location?location={}", location)
        val rooms = manageRoomUseCase.findRoomsByLocation(location)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @GetMapping("/search")
    fun searchRooms(@RequestParam query: String): ResponseEntity<List<RoomResponse>> {
        logger.debug("GET /api/rooms/search?query={}", query)
        val rooms = manageRoomUseCase.searchRooms(query)
        return ResponseEntity.ok(rooms.map(roomDtoMapper::toResponse))
    }
    
    @PostMapping
    fun createRoom(@RequestBody request: CreateRoomRequest): ResponseEntity<RoomResponse> {
        logger.info("POST /api/rooms - Creating room: name={}", request.name)
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
        logger.info("PUT /api/rooms/{} - Updating room", id)
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
        logger.info("DELETE /api/rooms/{} - Deleting room", id)
        manageRoomUseCase.deleteRoom(id)
        return ResponseEntity.noContent().build()
    }
}