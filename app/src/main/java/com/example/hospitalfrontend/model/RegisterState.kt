package com.example.hospitalfrontend.model

import java.util.Date

data class RegisterState(
    val id: Int,
    val auxiliary: AuxiliaryState,
    val date: Date? = null,
    val patient: PatientState,
    val hygieneType: HygieneState? = null,
    val diet: DietState? = null,
    val drain: DrainState? = null,
    val mobilization: MobilizationState? = null,
    val vitalSign: VitalSignState?,
    val observation: String?
)

