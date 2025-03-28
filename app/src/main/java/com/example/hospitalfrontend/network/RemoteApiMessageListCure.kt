package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.RegisterState
import com.example.hospitalfrontend.model.VitalSignState

interface RemoteApiMessageListCure {
    data class Success(val message: List<VitalSignState>) : RemoteApiMessageListCure
    object Loading : RemoteApiMessageListCure
    object Error : RemoteApiMessageListCure
}