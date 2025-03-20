package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.RoomState

interface RemoteApiMessageListRoom {
        data class Success(val message: List<RoomState>) : RemoteApiMessageListRoom
        object Loading : RemoteApiMessageListRoom
        object Error : RemoteApiMessageListRoom


}