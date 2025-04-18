package com.example.hospitalfrontend.ui.nurses.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.model.AuxiliaryState
import com.example.hospitalfrontend.model.NurseState
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.model.RoomState
import com.example.hospitalfrontend.model.VitalSignState
import com.example.hospitalfrontend.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientViewModel : ViewModel(

) {
    private val _patientState = MutableStateFlow<PatientState?>(null)
    val patientState: StateFlow<PatientState?> get() = _patientState.asStateFlow()

    // Variable for a list of rooms
    private val _rooms = MutableStateFlow<List<RoomState>>(listOf())
    val rooms: MutableStateFlow<List<RoomState>> = _rooms

    // Variable for a list of cures
    private val _cures = MutableStateFlow<List<VitalSignState>>(listOf())
    val cures: MutableStateFlow<List<VitalSignState>> = _cures

    // Load the list of cures
    fun loadCures(cure: List<VitalSignState>) {
        _cures.value = cure
    }

    // Load the list of the Nurse
    fun loadRooms(room: List<RoomState>) {
        _rooms.value = room
    }

    fun setPatientData(patient: PatientState) {
        _patientState.value = patient
    }
}