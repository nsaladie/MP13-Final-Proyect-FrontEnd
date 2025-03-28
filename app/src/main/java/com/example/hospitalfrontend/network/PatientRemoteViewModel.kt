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

    // Clear the API message
    fun clearApiMessage() {
        remoteApiMessage.value = RemoteApiMessagePatient.Loading
        remoteApiListMessage.value = RemoteApiMessageListRoom.Loading
    }

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun getAllRooms() {
        viewModelScope.launch {
            remoteApiListMessage.value = RemoteApiMessageListRoom.Loading
            try {
                val response = apiService.getAllRooms()
                remoteApiListMessage.value =
                    RemoteApiMessageListRoom.Success(response)
            } catch (e: Exception) {
                remoteApiListMessage.value = RemoteApiMessageListRoom.Error // Error response
            }
        }
    }

    fun getAllCures(id: Int) {
        viewModelScope.launch {
            Log.d("Error Lo", "Entra en el metodp")
            remoteApiListMessageCure.value = RemoteApiMessageListCure.Loading
            try {
                Log.d("Error Lo", "Entra en el try")
                val response = apiService.getAllCures(id)
                remoteApiListMessageCure.value =
                    RemoteApiMessageListCure.Success(response)
                Log.d("Error Lo", response.toString())
            } catch (e: Exception) {
                Log.d("Error", "error cura")
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
                remoteApiMessage.value = RemoteApiMessagePatient.Error
            }
        }
    }

}