package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListMedication
import kotlinx.coroutines.launch

class MedicationRemoteViewModel : ViewModel() {

    var remoteListMedication = mutableStateOf<RemoteApiMessageListMedication>(
        RemoteApiMessageListMedication.Loading
    )

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
}