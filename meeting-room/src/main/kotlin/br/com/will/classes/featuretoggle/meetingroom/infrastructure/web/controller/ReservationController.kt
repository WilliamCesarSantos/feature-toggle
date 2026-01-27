package br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.controller

import br.com.will.classes.featuretoggle.meetingroom.application.port.input.ManageReservationUseCase
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.CreateReservationRequest
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.dto.ReservationResponse
import br.com.will.classes.featuretoggle.meetingroom.infrastructure.web.mapper.ReservationDtoMapper
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val manageReservationUseCase: ManageReservationUseCase,
    private val reservationDtoMapper: ReservationDtoMapper
) {

    @GetMapping("/{id}")
    fun getReservationById(@PathVariable id: Long): ResponseEntity<ReservationResponse> {
        val reservation = manageReservationUseCase.getReservationById(id)
        return ResponseEntity.ok(reservationDtoMapper.toResponse(reservation))
    }
    
    @GetMapping("/room/{roomId}")
    fun getReservationsByRoom(@PathVariable roomId: Long): ResponseEntity<List<ReservationResponse>> {
        val reservations = manageReservationUseCase.findReservationsByRoom(roomId)
        return ResponseEntity.ok(reservations.map(reservationDtoMapper::toResponse))
    }
    
    @GetMapping("/requester")
    fun getReservationsByRequester(@RequestParam requester: String): ResponseEntity<List<ReservationResponse>> {
        val reservations = manageReservationUseCase.findReservationsByRequester(requester)
        return ResponseEntity.ok(reservations.map(reservationDtoMapper::toResponse))
    }
    
    @GetMapping("/room/{roomId}/conflicts")
    fun checkTimeConflicts(
        @PathVariable roomId: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime
    ): ResponseEntity<List<ReservationResponse>> {
        val conflictingReservations = manageReservationUseCase.findReservationsByTimeRange(
            roomId, startTime, endTime
        )
        return ResponseEntity.ok(conflictingReservations.map(reservationDtoMapper::toResponse))
    }
    
    @PostMapping
    fun createReservation(@RequestBody request: CreateReservationRequest): ResponseEntity<ReservationResponse> {
        val reservation = manageReservationUseCase.createReservation(
            roomId = request.roomId,
            participants = request.participants,
            startTime = request.startTime,
            endTime = request.endTime,
            requester = request.requester,
            notes = request.notes
        )
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(reservationDtoMapper.toResponse(reservation))
    }
    
    @DeleteMapping("/{id}")
    fun cancelReservation(@PathVariable id: Long): ResponseEntity<Void> {
        manageReservationUseCase.cancelReservation(id)
        return ResponseEntity.noContent().build()
    }
}