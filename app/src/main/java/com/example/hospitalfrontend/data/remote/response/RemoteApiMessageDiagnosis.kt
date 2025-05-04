package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.medical.DiagnosisState

interface RemoteApiMessageDiagnosis {
    data class Success(val message: DiagnosisState) : RemoteApiMessageDiagnosis
    object SuccessCreation : RemoteApiMessageDiagnosis
    object Loading : RemoteApiMessageDiagnosis
    object Error : RemoteApiMessageDiagnosis
    object NotFound : RemoteApiMessageDiagnosis
    object Idle : RemoteApiMessageDiagnosis
}