package com.example.hospitalfrontend.network

import android.util.Log
import retrofit2.Retrofit
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.model.DetailDiagnosisRequestData
import com.example.hospitalfrontend.model.DiagnosisRequest
import com.example.hospitalfrontend.model.DiagnosisRequestData
import com.example.hospitalfrontend.model.DiagnosisState
import com.example.hospitalfrontend.model.RegisterState
import com.example.hospitalfrontend.ui.nurses.viewmodels.DiagnosisViewModel
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

class DiagnosisRemoteViewModel : ViewModel() {
    var remoteApiMessageDiagnosis =
        mutableStateOf<RemoteApiMessageDiagnosis>(RemoteApiMessageDiagnosis.Idle)

    fun clearApiMessage() {
        remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Idle
    }


    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()).
        build().create(ApiService::class.java)


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
                    register = registerState,
                    diagnosis = diagnosisData
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


}