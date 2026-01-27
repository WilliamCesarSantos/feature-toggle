package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reservations")
class ReservationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "room_id", nullable = false)
    val roomId: Long,
    
    @Column(nullable = false)
    val participants: Int,
    
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,
    
    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,
    
    @Column(nullable = false)
    val requester: String,
    
    @Column
    val notes: String?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)