package com.example.hospitalfrontend.model

import java.util.Date

data class PatientState(
    val name: String,
    val surname: String,
    val address: String,
    val dateBirth: String,
    val language: String,
    val history: String,
    val caragiverName: String,
    val caragiverNumber: String,
    val allergy: String,
    val dateEntry: Date
)