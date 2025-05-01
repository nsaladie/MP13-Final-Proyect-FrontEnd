package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.DiagnosisState

interface RemoteApiMessageDiagnosis {
    data class Success(val message: DiagnosisState) : RemoteApiMessageDiagnosis
    object SuccessCreation : RemoteApiMessageDiagnosis
    object Loading : RemoteApiMessageDiagnosis
    object Error : RemoteApiMessageDiagnosis
    object NotFound : RemoteApiMessageDiagnosis
    object Idle : RemoteApiMessageDiagnosis
}