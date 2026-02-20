package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "meeting_rooms")
class MeetingRoomJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val name: String,
    
    @Column(nullable = false)
    val capacity: Int,
    
    @Column
    val location: String?,
    
    @Column
    val description: String?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)