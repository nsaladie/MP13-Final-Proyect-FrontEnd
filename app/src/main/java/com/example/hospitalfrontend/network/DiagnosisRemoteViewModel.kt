package com.example.hospitalfrontend.network

import android.util.Log
import retrofit2.Retrofit
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.ui.nurses.viewmodels.DiagnosisViewModel
import kotlinx.coroutines.launch
import retrofit2.converter.gson.GsonConverterFactory

class DiagnosisRemoteViewModel : ViewModel() {
    var remoteApiMessageDiagnosis =
        mutableStateOf<RemoteApiMessageDiagnosis>(RemoteApiMessageDiagnosis.Loading)

    fun clearApiMessage() {
        remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Loading
    }

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)


    fun getDiagnosisById(patientId: Int, diagnosisViewModel: DiagnosisViewModel) {
        viewModelScope.launch {
            remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Loading

            try {
                val response = apiService.getDiagnosis(patientId)
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Success(response)
                diagnosisViewModel.setDiagnosisDetail(response)
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
                remoteApiMessageDiagnosis.value = RemoteApiMessageDiagnosis.Error
            }
        }
    }
}