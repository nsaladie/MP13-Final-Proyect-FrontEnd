package com.example.hospitalfrontend.data.remote.response


interface RemoteApiMessageBoolean {
    data class Success(val message: Boolean) : RemoteApiMessageBoolean
    object Loading : RemoteApiMessageBoolean
    object Error : RemoteApiMessageBoolean
}