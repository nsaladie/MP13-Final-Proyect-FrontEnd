package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListDietTexture
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListDietType
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DietRemoteViewModel : ViewModel() {
    var remoteDietType =
        mutableStateOf<RemoteApiMessageListDietType>(RemoteApiMessageListDietType.Loading)
    var remoteDietTexture = mutableStateOf<RemoteApiMessageListDietTexture>(
        RemoteApiMessageListDietTexture.Loading
    )

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)

    fun getDietType() {
        viewModelScope.launch {
            remoteDietType.value = RemoteApiMessageListDietType.Loading
            try {
                val response = apiService.getAllDietType()
                remoteDietType.value = RemoteApiMessageListDietType.Success(response)
            } catch (e: Exception) {
                Log.d("Error dietType", e.toString())
                remoteDietType.value = RemoteApiMessageListDietType.Error // Error response
            }
        }
    }

    fun getDietTexture() {
        viewModelScope.launch {
            remoteDietTexture.value = RemoteApiMessageListDietTexture.Loading
            try {
                val response = apiService.getAllDietTexture()
                remoteDietTexture.value = RemoteApiMessageListDietTexture.Success(response)
            } catch (e: Exception) {
                Log.d("Error dietTexture", e.toString())
                remoteDietTexture.value = RemoteApiMessageListDietTexture.Error // Error response
            }
        }
    }
}