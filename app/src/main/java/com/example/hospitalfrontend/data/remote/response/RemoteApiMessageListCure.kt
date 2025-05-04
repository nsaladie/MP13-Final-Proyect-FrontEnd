package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.medical.VitalSignState

interface RemoteApiMessageListCure {
    data class Success(val message: List<VitalSignState>) : RemoteApiMessageListCure
    object Loading : RemoteApiMessageListCure
    object Error : RemoteApiMessageListCure
}