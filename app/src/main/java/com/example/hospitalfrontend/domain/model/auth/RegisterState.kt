package com.example.hospitalfrontend.domain.model.auth

import com.example.hospitalfrontend.domain.model.user.AuxiliaryState
import com.example.hospitalfrontend.domain.model.diet.DietState
import com.example.hospitalfrontend.domain.model.medical.DrainState
import com.example.hospitalfrontend.domain.model.medical.HygieneState
import com.example.hospitalfrontend.domain.model.medical.MobilizationState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.domain.model.medical.VitalSignState
import java.time.OffsetDateTime

data class RegisterState(
    val id: Int = 0,
    val auxiliary: AuxiliaryState,
    val date: OffsetDateTime? = null,
    val patient: PatientState,
    val hygieneType: HygieneState? = null,
    val diet: DietState? = null,
    val drain: DrainState? = null,
    val mobilization: MobilizationState? = null,
    val vitalSign: VitalSignState?,
    val observation: String?
)

