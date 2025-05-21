package com.example.hospitalfrontend.data.remote.response


interface RemoteApiMessageCreateMedication {
    data class Success(val message: Boolean) : RemoteApiMessageCreateMedication
    object Loading : RemoteApiMessageCreateMedication
    object Error : RemoteApiMessageCreateMedication
    object Idle : RemoteApiMessageCreateMedication
}