package com.example.hospitalfrontend.data.remote.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageBoolean
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCureDetail
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListCure
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListRoom
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessagePatient
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
import com.example.hospitalfrontend.utils.OffsetDateTimeAdapter
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime


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

    val gsonDate = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @RequiresApi(Build.VERSION_CODES.O)
    val gsonFormat =
        GsonBuilder().registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
            .create()

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create(gsonDate)).build()
        .create(ApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    private val apiServiceHour: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create(gsonFormat))
        .build()
        .create(ApiService::class.java)


    fun getAllRooms() {
        viewModelScope.launch {
            remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Loading
            try {
                val response = apiService.getAllRooms()
                remoteApiListMessageRoom.value = RemoteApiMessageListRoom.Success(response)
            } catch (e: Exception) {
                Log.d("Error list room", e.toString())
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
        viewModelScope.launch {
            remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Loading
            try {
                val response = apiService.createCure(registerState)
                remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Success(response)
            } catch (e: Exception) {
                Log.d("Error create", e.toString())
                remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Error
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCureDetail(cureId: Int) {
        viewModelScope.launch {
            try {
                val response = apiServiceHour.getCureDetail(cureId)
                remoteApiCureDetail.value = RemoteApiMessageCureDetail.Success(response)
            } catch (e: Exception) {
                Log.d("Error cure detail", e.toString())
                remoteApiCureDetail.value = RemoteApiMessageCureDetail.Error
            }
        }
    }
}