package com.example.hospitalfrontend.network

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.model.RegisterState
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PatientRemoteViewModel : ViewModel() {
    var remoteApiMessage = mutableStateOf<RemoteApiMessagePatient>(RemoteApiMessagePatient.Loading)
    var remoteApiListMessageRoom =
        mutableStateOf<RemoteApiMessageListRoom>(RemoteApiMessageListRoom.Loading)
    var remoteApiListMessageCure =
        mutableStateOf<RemoteApiMessageListCure>(RemoteApiMessageListCure.Loading)
    var remoteApiMessageBoolean =
        mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)

    var remoteApiCureDetail =
        mutableStateOf<RemoteApiMessageCureDetail>(RemoteApiMessageCureDetail.Loading)

    // Clear the API message
    fun clearApiMessage() {
        remoteApiMessage.value = RemoteApiMessagePatient.Loading
        remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Loading
        remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Loading
    }

    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiService::class.java)


    fun getAllRooms() {
        viewModelScope.launch {
            remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Loading
            try {
                val response = apiService.getAllRooms()
                remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Success(response)
                Log.d("Error Hab", response.toString())
            } catch (e: Exception) {
                Log.d("Error Hab Fail", e.toString())
                remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Error // Error response
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

    fun createCure(registerState: RegisterState) {
        Log.d("Error test", registerState.toString())
        viewModelScope.launch {
            remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Loading
            try {
                val response = apiService.createCure(registerState)
                remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Success(response)
                Log.d("Error create Su", response.toString())
            } catch (e: Exception) {
                Log.d("Error create", e.toString())
                remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Error
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