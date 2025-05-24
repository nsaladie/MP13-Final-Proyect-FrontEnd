package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.patient.PatientState

interface RemoteApiMessageListPatient {
    data class Success(val message: List<PatientState>) : RemoteApiMessageListPatient
    object Loading : RemoteApiMessageListPatient
    object Error : RemoteApiMessageListPatient
}