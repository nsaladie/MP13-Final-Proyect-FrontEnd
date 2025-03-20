package com.example.hospitalfrontend.model

import java.util.Date

data class RoomState(
    val roomId: String,
    val roomNumber: Int,
    val timeInRoom: Date,
    val patient: PatientState ?
)
