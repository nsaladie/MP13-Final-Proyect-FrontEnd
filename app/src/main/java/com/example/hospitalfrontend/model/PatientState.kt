package com.example.hospitalfrontend.model

import okhttp3.Address
import java.util.Date

data class PatientState (
    val name: String,
    val surname:String,
    val address: String,
    val birthday: String,
    val language: String,
    val antecedentsMedics: String,
    val dataCaregiver:String,
val allergies: String
    )