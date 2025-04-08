package com.example.hospitalfrontend.network

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PatientRemoteViewModel : ViewModel() {
    var remoteApiMessage = mutableStateOf<RemoteApiMessagePatient>(RemoteApiMessagePatient.Loading)
    var remoteApiListMessage =
        mutableStateOf<RemoteApiMessageListRoom>(RemoteApiMessageListRoom.Loading)
    var remoteApiListMessageCure =
        mutableStateOf<RemoteApiMessageListCure>(RemoteApiMessageListCure.Loading)

    var remoteApiCureDetail =
        mutableStateOf<RemoteApiMessageCureDetail>(RemoteApiMessageCureDetail.Loading)

    // Clear the API message
    fun clearApiMessage() {
        remoteApiMessage.value = RemoteApiMessagePatient.Loading
        remoteApiListMessage.value = RemoteApiMessageListRoom.Loading
    }

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)

    fun getAllRooms() {
        viewModelScope.launch {
            remoteApiListMessage.value = RemoteApiMessageListRoom.Loading
            try {
                val response = apiService.getAllRooms()
                remoteApiListMessage.value = RemoteApiMessageListRoom.Success(response)
            } catch (e: Exception) {
                Log.d("Error rooms", e.toString())
                remoteApiListMessage.value = RemoteApiMessageListRoom.Error // Error response
            }
        }
    }

    fun getAllCures(id: Int) {
        viewModelScope.launch {
            remoteApiListMessageCure.value = RemoteApiMessageListCure.Loading
            try {
                val response = apiService.getAllCures(id)
                remoteApiListMessageCure.value = RemoteApiMessageListCure.Success(response)
            } catch (e: Exception) {
                Log.d("Error list cures", "Error ${e.toString()}")
                remoteApiListMessageCure.value = RemoteApiMessageListCure.Error // Error response
            }
        }
    }

    fun getPatientById(patientId: Int, patientViewModel: PatientViewModel) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessagePatient.Loading
            try {
                val response = apiService.getPatientById(patientId)
                remoteApiMessage.value = RemoteApiMessagePatient.Success(response)
                patientViewModel.setPatientData(response)
            } catch (e: Exception) {
                Log.d("Error info patient", e.toString())
                remoteApiMessage.value = RemoteApiMessagePatient.Error
            }
        }
    }

    fun getCureDetail(cureId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getCureDetail(cureId)
                remoteApiCureDetail.value = RemoteApiMessageCureDetail.Success(response)
                Log.d("Error", response.toString())
            } catch (e: Exception) {
                Log.d("Error cure detail", e.toString())
                remoteApiCureDetail.value = RemoteApiMessageCureDetail.Error
            }
        }
    }

}