package com.example.hospitalfrontend.network

import RemoteApiMessageAuxiliary
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.model.LoginAuxiliary
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AuxiliaryRemoteViewModel  : ViewModel() {

    //Auxiliar login
    var remoteApiMessageAuxiliary =
        mutableStateOf<RemoteApiMessageAuxiliary>(RemoteApiMessageAuxiliary.Loading)


    // Clear the API message
    fun clearApiMessage() {
        //Auxiliar
        remoteApiMessageAuxiliary.value = RemoteApiMessageAuxiliary.Loading
    }

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)


    fun loginAuxiliary(auxiliaryId: Int) {
        viewModelScope.launch {
            remoteApiMessageAuxiliary.value = RemoteApiMessageAuxiliary.Loading
            try {
                val response = apiService.loginAuxiliary(LoginAuxiliary(auxiliaryId))
                remoteApiMessageAuxiliary.value = RemoteApiMessageAuxiliary.Success(response)
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
                remoteApiMessageAuxiliary.value =
                    RemoteApiMessageAuxiliary.Error
            }
        }
    }
}


