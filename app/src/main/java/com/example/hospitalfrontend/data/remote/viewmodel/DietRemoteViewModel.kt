package com.example.hospitalfrontend.data.remote.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.data.api.ApiService
import com.example.hospitalfrontend.data.remote.response.*
import com.example.hospitalfrontend.domain.model.diet.DietTextureTypeState
import com.example.hospitalfrontend.domain.model.diet.DietTypeState
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DietRemoteViewModel : ViewModel() {
    var remoteDietType =
        mutableStateOf<RemoteApiMessageListDietType>(RemoteApiMessageListDietType.Loading)
    var remoteDietTexture = mutableStateOf<RemoteApiMessageListDietTexture>(
        RemoteApiMessageListDietTexture.Loading
    )
    var remoteNewDietType = mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)
    var remoteNewDietTexture =
        mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)

    fun cleanMessage() {
        remoteNewDietType.value =
            RemoteApiMessageBoolean.Loading
        remoteNewDietTexture.value =
            RemoteApiMessageBoolean.Loading
    }

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

    fun addDietTexture(text: String) {
        var dietTexture = DietTextureTypeState(description = text)
        val listTexture: MutableList<DietTextureTypeState> = mutableListOf()

        viewModelScope.launch {
            try {
                listTexture.add(dietTexture)

                val response = apiService.createNewDietTexture(listTexture)
                remoteNewDietTexture.value = RemoteApiMessageBoolean.Success(response)
            } catch (e: Exception) {
                Log.d("Error add new dietTexture", e.toString())
                remoteNewDietTexture.value = RemoteApiMessageBoolean.Error
            }
        }
    }

    fun addDietType(text: String) {
        var dietType = DietTypeState(description = text)
        val listType: MutableList<DietTypeState> = mutableListOf()

        viewModelScope.launch {
            try {
                listType.add(dietType)

                val response = apiService.createNewDietType(listType)
                remoteNewDietType.value = RemoteApiMessageBoolean.Success(response)
            } catch (e: Exception) {
                Log.d("Error add new dietTexture", e.toString())
                remoteNewDietType.value = RemoteApiMessageBoolean.Error
            }
        }
    }
}