package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.facility.RoomState
import com.example.hospitalfrontend.domain.model.facility.RoomDTO

interface RemoteApiMessageListRoom {
        data class Success(val message: List<RoomDTO>) : RemoteApiMessageListRoom
        object Loading : RemoteApiMessageListRoom
        object Error : RemoteApiMessageListRoom
}