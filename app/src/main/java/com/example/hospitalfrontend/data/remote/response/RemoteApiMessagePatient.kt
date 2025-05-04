package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.patient.PatientState

sealed interface RemoteApiMessagePatient {
    data class Success(val message: PatientState) : RemoteApiMessagePatient
    object Loading : RemoteApiMessagePatient
    object Error : RemoteApiMessagePatient
}