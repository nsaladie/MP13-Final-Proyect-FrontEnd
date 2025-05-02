package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.DietTypeState

interface RemoteApiMessageListDietType {
    data class Success(val message: List<DietTypeState>) : RemoteApiMessageListDietType
    object Loading : RemoteApiMessageListDietType
    object Error : RemoteApiMessageListDietType
}