package com.example.hospitalfrontend.domain.model.facility

import com.example.hospitalfrontend.domain.model.patient.PatientState
import java.util.Date

data class RoomState(
    val roomId: String,
    val roomNumber: Int,
    val timeInRoom: Date,
    val patient: PatientState?
)
