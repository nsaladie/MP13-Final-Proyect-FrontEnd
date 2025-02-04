package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.NurseState

interface RemoteApiMessageListNurse {
    data class Success(val message: List<NurseState>) : RemoteApiMessageListNurse
    object Loading : RemoteApiMessageListNurse
    object Error : RemoteApiMessageListNurse

}
