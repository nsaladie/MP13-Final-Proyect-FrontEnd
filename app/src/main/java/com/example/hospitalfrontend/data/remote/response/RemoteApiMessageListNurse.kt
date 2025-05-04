package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.user.NurseState

interface RemoteApiMessageListNurse {
    data class Success(val message: List<NurseState>) : RemoteApiMessageListNurse
    object Loading : RemoteApiMessageListNurse
    object Error : RemoteApiMessageListNurse

}
