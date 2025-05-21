package com.example.hospitalfrontend.data.remote.response

interface RemoteApiMessagePatientUpdate {
    data class Success(val message: Boolean) : RemoteApiMessagePatientUpdate
    object Loading : RemoteApiMessagePatientUpdate
    object Error : RemoteApiMessagePatientUpdate
}