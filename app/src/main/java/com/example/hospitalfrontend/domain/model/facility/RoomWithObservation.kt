package com.example.hospitalfrontend.domain.model.facility

import com.example.hospitalfrontend.domain.model.patient.PatientState
import java.util.Date

data class RoomDTO(
    val room: RoomState? = null,
    val patient: PatientState? = null,
    val assignmentDate: Date? = null,
    val releaseDate: Date? = null,
    val occupied: Boolean? = null,
    val lastObservation: String? = null
)