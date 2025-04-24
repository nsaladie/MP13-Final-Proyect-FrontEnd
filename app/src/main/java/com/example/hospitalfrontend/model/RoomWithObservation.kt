package com.example.hospitalfrontend.model

data class RoomWithObservation(
    val room: RoomState,
    val lastObservation: String?
)