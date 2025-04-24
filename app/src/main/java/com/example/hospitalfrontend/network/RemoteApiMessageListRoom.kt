package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.RoomWithObservation

interface RemoteApiMessageListRoom {
        data class Success(val message: List<RoomWithObservation>) : RemoteApiMessageListRoom
        object Loading : RemoteApiMessageListRoom
        object Error : RemoteApiMessageListRoom


}