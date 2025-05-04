package com.example.hospitalfrontend.domain.model.facility

data class RoomWithObservation(
    val room: RoomState,
    val lastObservation: String?
)