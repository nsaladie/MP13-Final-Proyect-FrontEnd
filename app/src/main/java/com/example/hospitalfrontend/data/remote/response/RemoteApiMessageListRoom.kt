package com.example.hospitalfrontend.data.remote.response

import com.example.hospitalfrontend.domain.model.facility.RoomWithObservation

interface RemoteApiMessageListRoom {
        data class Success(val message: List<RoomWithObservation>) : RemoteApiMessageListRoom
        object Loading : RemoteApiMessageListRoom
        object Error : RemoteApiMessageListRoom


}