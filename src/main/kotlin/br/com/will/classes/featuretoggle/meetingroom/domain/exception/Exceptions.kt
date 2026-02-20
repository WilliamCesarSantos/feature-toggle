package br.com.will.classes.featuretoggle.meetingroom.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class InvalidRoomException(message: String) : DomainException(message)

class InvalidReservationException(message: String) : DomainException(message)

class ResourceNotFoundException(resourceType: String, id: Long) : 
    DomainException("$resourceType with id $id not found")

class ConflictException(message: String) : DomainException(message)