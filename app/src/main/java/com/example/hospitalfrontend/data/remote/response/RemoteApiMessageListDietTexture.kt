package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.diet.DietTextureTypeState

interface RemoteApiMessageListDietTexture {
    data class Success(val message: List<DietTextureTypeState>) : RemoteApiMessageListDietTexture
    object Loading : RemoteApiMessageListDietTexture
    object Error : RemoteApiMessageListDietTexture
}