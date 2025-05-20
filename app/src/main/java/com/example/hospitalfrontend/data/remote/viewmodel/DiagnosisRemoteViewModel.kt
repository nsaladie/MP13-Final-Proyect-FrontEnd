package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import retrofit2.Retrofit
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.domain.model.medical.DetailDiagnosisRequestData
import com.example.hospitalfrontend.domain.model.medical.DiagnosisRequest
import com.example.hospitalfrontend.domain.model.medical.DiagnosisRequestData
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.ui.diagnosis.viewmodel.DiagnosisViewModel
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

class DiagnosisRemoteViewModel : ViewModel() {
    var remoteApiMessageDiagnosis =
        mutableStateOf<RemoteApiMessageDiagnosis>(RemoteApiMessageDiagnosis.Idle)

    fun clearApiMessage() {
        remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Idle
    }


    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)


    fun getDiagnosisById(patientId: Int, diagnosisViewModel: DiagnosisViewModel) {
        viewModelScope.launch {
            remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Loading

            try {
                val response = apiService.getDiagnosis(patientId)
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Success(response)
                diagnosisViewModel.setDiagnosisDetail(response)
            } catch (e: Exception) {
                Log.d("ERROR Dia", e.toString())
                diagnosisViewModel.setDiagnosisDetail(null)
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.NotFound
            }
        }
    }

    fun createDiagnosis(registerState: RegisterState, diagnosisState: DiagnosisState) {
        viewModelScope.launch {
            remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Loading

            try {
                val detailDiagnosis = DetailDiagnosisRequestData(
                    dependencyLevel = diagnosisState.dependencyLevel,
                    oxygenLevel = diagnosisState.oxygenLevel,
                    oxygenLevelDescription = diagnosisState.oxygenLevelDescription,
                    diapers = diagnosisState.diapers,
                    totalChangesDiapers = diagnosisState.totalChangesDiapers,
                    detailDescription = diagnosisState.detailDescription,
                    urinaryCatheter = diagnosisState.urinaryCatheter,
                    rectalCatheter = diagnosisState.rectalCatheter,
                    nasogastricTube = diagnosisState.nasogastricTube
                )

                val diagnosisData = DiagnosisRequestData(
                    detailDiagnosisSet = listOf(detailDiagnosis)
                )

                val request = DiagnosisRequest(
                    register = registerState, diagnosis = diagnosisData
                )
                val response = apiService.createDiagnosis(request)

                if (response) {
                    remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.SuccessCreation
                } else {
                    remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Error
                }
            } catch (e: Exception) {
                Log.d("ERROR DiagnosisCreate", e.toString())
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Error
            }
        }
    }

    fun getDiagnosisListById(patientId: Int, diagnosisViewModel: DiagnosisViewModel) {
        viewModelScope.launch {
            remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Loading

            try {
                val response = apiService.getAllDiagnosis(patientId)
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.SuccessList(response)
                Log.d("Entra", "Hola")
            } catch (e: Exception) {
                Log.d("ERROR DiagList", e.toString())
                diagnosisViewModel.setDiagnosisDetail(null)
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.NotFound
            }
        }
    }


}