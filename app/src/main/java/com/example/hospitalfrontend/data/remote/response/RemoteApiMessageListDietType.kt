package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.diet.DietTypeState

interface RemoteApiMessageListDietType {
    data class Success(val message: List<DietTypeState>) : RemoteApiMessageListDietType
    object Loading : RemoteApiMessageListDietType
    object Error : RemoteApiMessageListDietType
}