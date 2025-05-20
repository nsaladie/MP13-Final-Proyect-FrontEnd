package com.example.hospitalfrontend.domain.model.medication

data class MedicationState(
    val id: Int,
    val name: String,
    val dosage: String,
    val adminstrationRoute: String,
    val stock: Int
)
