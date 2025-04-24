package com.example.hospitalfrontend.model

data class VitalSignState(
    val id: Int = 0,
    val systolicBloodPressure: Double = 0.0,
    val diastolicBloodPressure: Double = 0.0,
    val respiratoryRate: Double = 0.0,
    val pulse: Double = 0.0,
    val temperature: Double = 0.0,
    val oxygenSaturation: Double = 0.0,
    val urineVolume: Double = 0.0,
    val bowelMovements: Double = 0.0,
    val serumTherapy: Double = 0.0
)
