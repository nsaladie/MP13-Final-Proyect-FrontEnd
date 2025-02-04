package com.example.hospitalfrontend.network


interface RemoteApiMessageBoolean {
    data class Success(val message: Boolean) : RemoteApiMessageBoolean
    object Loading : RemoteApiMessageBoolean
    object Error : RemoteApiMessageBoolean
}