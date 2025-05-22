package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.medication.MedicationState

interface RemoteApiMessageMedication {
    data class Success(val message: MedicationState) : RemoteApiMessageMedication
    object Loading : RemoteApiMessageMedication
    object Error : RemoteApiMessageMedication
}