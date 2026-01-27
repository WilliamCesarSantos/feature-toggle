package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.repository

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity.ReservationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ReservationJpaRepository : JpaRepository<ReservationJpaEntity, Long> {
    fun findByRoomId(roomId: Long): List<ReservationJpaEntity>
    fun findByRequesterContainingIgnoreCase(requester: String): List<ReservationJpaEntity>
    
    @Query("""
        SELECT r FROM ReservationJpaEntity r 
        WHERE r.roomId = :roomId 
        AND ((r.startTime <= :endTime AND r.endTime >= :startTime))
    """)
    fun findConflictingReservations(
        @Param("roomId") roomId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<ReservationJpaEntity>
}