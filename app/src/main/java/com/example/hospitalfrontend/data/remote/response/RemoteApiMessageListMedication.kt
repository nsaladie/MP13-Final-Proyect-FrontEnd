package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.medication.MedicationState

interface RemoteApiMessageListMedication {
    data class Success(val message: List<MedicationState>) : RemoteApiMessageListMedication
    object Loading : RemoteApiMessageListMedication
    object Error : RemoteApiMessageListMedication
}