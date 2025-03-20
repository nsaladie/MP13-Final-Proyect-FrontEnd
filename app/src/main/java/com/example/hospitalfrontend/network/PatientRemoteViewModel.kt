package com.example.hospitalfrontend.network

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PatientRemoteViewModel : ViewModel(){

    var remoteApiListMessage =
        mutableStateOf<RemoteApiMessageListRoom>(RemoteApiMessageListRoom.Loading)

    // Clear the API message
    fun clearApiMessage() {
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

}