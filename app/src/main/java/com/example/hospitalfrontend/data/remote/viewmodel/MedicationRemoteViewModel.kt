package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCreateMedication
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListMedication
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import kotlinx.coroutines.launch

class MedicationRemoteViewModel : ViewModel() {

    var remoteListMedication = mutableStateOf<RemoteApiMessageListMedication>(
        RemoteApiMessageListMedication.Loading
    )
    var remoteCreateMedication = mutableStateOf<RemoteApiMessageCreateMedication>(
        RemoteApiMessageCreateMedication.Loading
    )
    fun clearApiMessage() {
        remoteCreateMedication.value = RemoteApiMessageCreateMedication.Idle
    }

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)


    fun getAllMedication() {
        viewModelScope.launch {
            remoteListMedication.value = RemoteApiMessageListMedication.Loading
            try {
                val response = apiService.getAllMedication()
                remoteListMedication.value = RemoteApiMessageListMedication.Success(response)
            } catch (e: Exception) {
                Log.d("Error List Medication", e.toString())
                remoteListMedication.value = RemoteApiMessageListMedication.Error
            }
        }
    }
    fun addMedicine(medicationState: MedicationState) {
        viewModelScope.launch {
            remoteCreateMedication.value = RemoteApiMessageCreateMedication.Loading
            try {
                val response = apiService.addMedicine(medicationState)
                remoteCreateMedication.value = RemoteApiMessageCreateMedication.Success(response)
                Log.d("Success add Medication", response.toString())

            } catch (e: Exception) {
                Log.d("Error add Medication", e.toString())
                remoteCreateMedication.value = RemoteApiMessageCreateMedication.Error
            }
        }
    }
}