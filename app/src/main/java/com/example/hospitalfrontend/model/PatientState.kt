package com.example.hospitalfrontend.model

import java.util.Date

data class PatientState(
    val historialNumber: Int,
    val name: String,
    val surname: String,
    val direction: String,
    val dateBirth: Date,
    val language: String,
    val history: String,
    val caragiverName: String,
    val caragiverNumber: String,
    val allergy: String,
    val dateEntry: Date
)