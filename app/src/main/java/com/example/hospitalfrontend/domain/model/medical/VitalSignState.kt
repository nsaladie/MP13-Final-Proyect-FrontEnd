package com.example.hospitalfrontend.domain.model.medical

data class VitalSignState(
    val id: Int = 0,
    val systolicBloodPressure: Double = 0.0,
    val diastolicBloodPressure: Double = 0.0,
    val respiratoryRate: Double = 0.0,
    val pulse: Double = 0.0,
    val temperature: Double = 0.0,
    val oxygenSaturation: Double = 0.0,
    val urineVolume: Double? = null,
    val bowelMovements: Double? = null,
    val serumTherapy: Double? = null
)