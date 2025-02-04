package com.example.hospitalfrontend.network

import android.net.Uri
import android.graphics.*
import android.util.Log
import retrofit2.Retrofit
import androidx.lifecycle.*
import okhttp3.MultipartBody
import android.content.Context
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.hospitalfrontend.model.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.converter.gson.GsonConverterFactory

class RemoteViewModel : ViewModel() {
    var remoteApiMessage = mutableStateOf<RemoteApiMessageNurse>(RemoteApiMessageNurse.Loading)
        private set
    var remoteApiListMessage =
        mutableStateOf<RemoteApiMessageListNurse>(RemoteApiMessageListNurse.Loading)
    var remoteApiMessageBoolean =
        mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)
    var remoteApiMessageUploadPhoto =
        mutableStateOf<RemoteApiMessageBoolean>(RemoteApiMessageBoolean.Loading)

    // Save in a list the image and the id of the nurse
    private var listNurseImage = mutableStateListOf<NurseProfileImageState>()


    // Clear the API message
    fun clearApiMessage() {
        remoteApiMessage.value = RemoteApiMessageNurse.Loading
        remoteApiListMessage.value = RemoteApiMessageListNurse.Loading
        remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Loading
        remoteApiMessageUploadPhoto.value = RemoteApiMessageBoolean.Loading
    }

    // Retrofit instance with ApiService creation for network requests
    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    // Function to get a nurse by ID
    fun getNurseById(nurseId: Int) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessageNurse.Loading
            try {
                val response = apiService.getNurseById(nurseId)
                remoteApiMessage.value = RemoteApiMessageNurse.Success(response) // Success response
            } catch (e: Exception) {
                remoteApiMessage.value = RemoteApiMessageNurse.Error // Error response
            }
        }
    }

    // Function to get all nurses
    fun getAllNurses() {
        viewModelScope.launch {
            remoteApiListMessage.value = RemoteApiMessageListNurse.Loading
            try {
                val response = apiService.getAll()
                remoteApiListMessage.value =
                    RemoteApiMessageListNurse.Success(response) // Success response

                // Load nurse images from the API response
                listNurseImage.clear()
                response.forEach { nurse ->
                    launch {
                        val bitmap = getPhotoById(nurse.id)
                        listNurseImage.add(NurseProfileImageState(nurse.id, bitmap))
                    }
                }
            } catch (e: Exception) {
                remoteApiListMessage.value = RemoteApiMessageListNurse.Error // Error response
            }
        }
    }

    // Function to login a nurse
    fun loginNurse(nurse: LoginRequest) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessageNurse.Loading // Show loading message
            try {
                val response = apiService.loginNurse(nurse)
                remoteApiMessage.value = RemoteApiMessageNurse.Success(response) // Success response
            } catch (e: Exception) {
                remoteApiMessage.value = RemoteApiMessageNurse.Error // Error response
            }
        }
    }

    // Function to create a new nurse
    fun createNurse(nurse: NurseState) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessageNurse.Loading // Show loading message
            try {
                val response = apiService.createNurse(nurse)
                remoteApiMessage.value = RemoteApiMessageNurse.Success(response) // Success response
            } catch (e: Exception) {
                remoteApiMessage.value = RemoteApiMessageNurse.Error // Error response
            }
        }
    }

    // Function to delete a nurse by ID
    fun deleteNurse(nurseId: Int) {
        viewModelScope.launch {
            remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Loading
            try {
                val response = apiService.deleteNurse(nurseId)
                remoteApiMessageBoolean.value =
                    RemoteApiMessageBoolean.Success(response) // Success response
            } catch (e: Exception) {
                remoteApiMessageBoolean.value = RemoteApiMessageBoolean.Error // Error response
            }
        }
    }

    // Function to find a nurse by name
    fun findByName(nurseName: String) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessageNurse.Loading
            try {
                val response = apiService.findByName(nurseName)
                remoteApiMessage.value = RemoteApiMessageNurse.Success(response) // Success response
            } catch (e: Exception) {
                remoteApiMessage.value = RemoteApiMessageNurse.Error // Error response
            }
        }
    }

    // Function to update nurse information
    fun updateNurse(nurseId: Int, updateNurse: NurseState) {
        viewModelScope.launch {
            remoteApiMessage.value = RemoteApiMessageNurse.Loading
            try {
                val connection = Retrofit.Builder().baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                val endPoint = connection.create(ApiService::class.java)
                val response = endPoint.updateNurse(nurseId, updateNurse)
                remoteApiMessage.value = RemoteApiMessageNurse.Success(response)
            } catch (e: Exception) {
                remoteApiMessage.value = RemoteApiMessageNurse.Error
                Log.e("RemoteViewModel", "Error updating nurse: ${e.message}")
            }
        }
    }

    // Function to get a nurse's photo by ID
    suspend fun getPhotoById(id: Int): Bitmap? {
        return try {
            val response = apiService.getPhotoById(id)
            if (response.isSuccessful) {
                // Convert the byte array from the response to a Bitmap
                response.body()?.bytes()?.let { bytes ->
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    // Function to upload a nurse's profile photo
    fun uploadPhoto(id: Int, photoUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                // Open an input stream from the provided URI and read the bytes
                val inputStream = context.contentResolver.openInputStream(photoUri)
                val byteArray = inputStream?.readBytes() ?: return@launch
                inputStream.close() // Close the input stream

                // Create a request body with the byte array
                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "profile.jpg", requestBody)

                // Make the API call to upload the photo
                val response = apiService.uploadPhotoById(id, body)

                // Return result for the UI
                if (response.isSuccessful) {
                    remoteApiMessageUploadPhoto.value = RemoteApiMessageBoolean.Success(true)
                } else {
                    remoteApiMessageUploadPhoto.value = RemoteApiMessageBoolean.Error
                }
            } catch (e: Exception) {
                remoteApiMessageUploadPhoto.value = RemoteApiMessageBoolean.Error // Set Error state
            }


        }
    }

    // Function to get cached photo
    fun getCachedPhoto(nurseId: Int): Bitmap? {
        return listNurseImage.find { it.nurseId == nurseId }?.image
    }
}