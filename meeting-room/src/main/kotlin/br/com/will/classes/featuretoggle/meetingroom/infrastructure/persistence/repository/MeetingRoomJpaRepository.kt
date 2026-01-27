package br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.repository

import br.com.will.classes.featuretoggle.meetingroom.infrastructure.persistence.entity.MeetingRoomJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MeetingRoomJpaRepository : JpaRepository<MeetingRoomJpaEntity, Long> {
    fun findByName(name: String): MeetingRoomJpaEntity?
    fun findByCapacityGreaterThanEqual(minCapacity: Int): List<MeetingRoomJpaEntity>
    fun findByLocationContainingIgnoreCase(location: String): List<MeetingRoomJpaEntity>
    fun findByNameContainingIgnoreCase(query: String): List<MeetingRoomJpaEntity>
    
    @Query("SELECT r FROM MeetingRoomJpaEntity r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameOrDescriptionContaining(query: String): List<MeetingRoomJpaEntity>
}