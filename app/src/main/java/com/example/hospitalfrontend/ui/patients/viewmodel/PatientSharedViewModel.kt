package com.example.hospitalfrontend.ui.patients.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.domain.model.facility.RoomDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PatientSharedViewModel : ViewModel() {

    private val _idsAsignados = MutableStateFlow<List<Int>>(emptyList())
    val idsAsignados: StateFlow<List<Int>> = _idsAsignados

    fun setIdsAsignados(ids: List<Int>) {
        _idsAsignados.value = ids
    }

    fun clearIdsAsignados() {
        _idsAsignados.value = emptyList()
    }

    fun updateIdsFromRooms(rooms: List<RoomDTO>) {
        val newIds = rooms.mapNotNull { it.patient?.historialNumber }
        _idsAsignados.value = newIds
    }
}
