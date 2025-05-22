package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageBoolean
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListMedication
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageMedication
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCreateMedication
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import kotlinx.coroutines.launch

class MedicationRemoteViewModel : ViewModel() {

    var remoteListMedication = mutableStateOf<RemoteApiMessageListMedication>(
        RemoteApiMessageListMedication.Loading
    )
    
    var remoteMedication =
        mutableStateOf<RemoteApiMessageMedication>(RemoteApiMessageMedication.Loading)

    var remoteUpdateMedication =
        mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)
        
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

    fun resetMessage() {
        remoteMedication.value = RemoteApiMessageMedication.Loading
        remoteUpdateMedication.value = RemoteApiMessageBoolean.Loading
    }

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

    fun getMedicationId(id: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getMedication(id)
                remoteMedication.value = RemoteApiMessageMedication.Success(response)
            } catch (e: Exception) {
                Log.d("Error Medication Id", e.toString())
                remoteMedication.value = RemoteApiMessageMedication.Error
            }
        }
    }

    fun updateMedication(id: Int, request: MedicationState) {
        viewModelScope.launch {
            try {
                val response = apiService.updateMedication(id, request)
                remoteUpdateMedication.value = RemoteApiMessageBoolean.Success(response)
            } catch (e: Exception) {
                Log.d("Error Medication Update", e.toString())
                remoteUpdateMedication.value = RemoteApiMessageBoolean.Error
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