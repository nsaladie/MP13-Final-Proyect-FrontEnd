package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.auth.RegisterState

interface RemoteApiMessageCureDetail {
    data class Success(val data: RegisterState) : RemoteApiMessageCureDetail
    object Loading : RemoteApiMessageCureDetail
    object Error : RemoteApiMessageCureDetail
}