package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.user.NurseState

sealed interface RemoteApiMessageNurse {
    data class Success(val message: NurseState) : RemoteApiMessageNurse
    object Loading : RemoteApiMessageNurse
    object Error : RemoteApiMessageNurse
}