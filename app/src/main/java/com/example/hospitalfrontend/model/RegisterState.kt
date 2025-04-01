package com.example.hospitalfrontend.model

import java.util.Date

data class RegisterState(
    val id: Int,
    val auxiliary: AuxiliaryState,
    val date: Date,
    val patient: PatientState,
    val hygieneType: HygieneState,
    val diet: DietState,
    val drain: DrainState,
    val mobilization: MobilizationState,
    val vitalSign: VitalSignState
)

