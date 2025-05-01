package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.DietTextureTypeState
import com.example.hospitalfrontend.model.DietTypeState

interface RemoteApiMessageListDietTexture {
    data class Success(val message: List<DietTextureTypeState>) : RemoteApiMessageListDietTexture
    object Loading : RemoteApiMessageListDietTexture
    object Error : RemoteApiMessageListDietTexture
}