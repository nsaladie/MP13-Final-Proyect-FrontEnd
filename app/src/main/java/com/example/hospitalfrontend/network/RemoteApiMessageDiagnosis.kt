package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.DiagnosisState

interface RemoteApiMessageDiagnosis {
    data class Success(val message: DiagnosisState) : RemoteApiMessageDiagnosis
    object Loading : RemoteApiMessageDiagnosis
    object Error : RemoteApiMessageDiagnosis
}