package com.example.hospitalfrontend.model

data class VitalSignState (
    val id: Int,
    val systolicBloodPressure: Double,
    val diastolicBloodPressure: Double,
    val respiratoryRate: Double,
    val pulse: Double,
    val temperature: Double,
    val oxygenSaturation: Double,
    val urineVolume: Double,
    val bowelMovements: Double,
    val serumTherapy: Double
)