package com.example.hospitalfrontend.domain.model.patient

import java.util.Date

data class PatientState(
    val historialNumber: Int = 0,
    val name: String = "",
    val surname: String = "",
    val direction: String = "",
    val dateBirth: Date? = null,
    val language: String = "",
    val history: String = "",
    val caragiverName: String = "",
    val caragiverNumber: String = "",
    val allergy: String = "",
    val dateEntry: Date? = null
)