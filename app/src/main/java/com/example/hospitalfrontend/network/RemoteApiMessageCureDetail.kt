package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.RegisterState

interface RemoteApiMessageCureDetail {
    data class Success(val data: RegisterState) : RemoteApiMessageCureDetail
    object Loading : RemoteApiMessageCureDetail
    object Error : RemoteApiMessageCureDetail
}