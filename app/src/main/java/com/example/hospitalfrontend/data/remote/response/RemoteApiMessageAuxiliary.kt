package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.user.AuxiliaryState

interface RemoteApiMessageAuxiliary {
        data class Success(val message: AuxiliaryState) : RemoteApiMessageAuxiliary
        object Loading : RemoteApiMessageAuxiliary
        object Error : RemoteApiMessageAuxiliary
        object InvalidCredentials : RemoteApiMessageAuxiliary
}