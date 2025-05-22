package com.example.hospitalfrontend.domain.model.facility

import com.example.hospitalfrontend.domain.model.patient.PatientState
import java.util.Date

data class RoomDTO(
    val room: RoomState,
    val patient: PatientState?,
    val assignmentDate: Date?,
    val releaseDate: Date,
    val occupied: Boolean,
    val lastObservation: String
)