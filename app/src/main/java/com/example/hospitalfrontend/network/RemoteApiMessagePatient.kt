package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.PatientState

sealed interface RemoteApiMessagePatient {
    data class Success(val message: PatientState) : RemoteApiMessagePatient
    object Loading : RemoteApiMessagePatient
    object Error : RemoteApiMessagePatient
}