package com.example.hospitalfrontend.ui.patients.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.facility.RoomWithObservation
import com.example.hospitalfrontend.domain.model.medical.VitalSignState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PatientViewModel : ViewModel(

) {
    private val _patientState = MutableStateFlow<PatientState?>(null)
    val patientState: StateFlow<PatientState?> get() = _patientState.asStateFlow()

    // Variable for a list of rooms
    private val _rooms = MutableStateFlow<List<RoomWithObservation>>(listOf())
    val rooms: MutableStateFlow<List<RoomWithObservation>> = _rooms

    private val _registers = MutableStateFlow<List<RegisterState>>(emptyList())
    val registers: StateFlow<List<RegisterState>> = _registers

    // Variable for a list of cures
    private val _cures = MutableStateFlow<List<VitalSignState>>(listOf())
    val cures: MutableStateFlow<List<VitalSignState>> = _cures

    // Load the list of cures
    fun loadCures(cure: List<VitalSignState>) {
        _cures.value = cure
    }

    // Load the list of the Nurse
    fun loadRooms(room: List<RoomWithObservation>) {
        _rooms.value = room
    }

    fun setPatientData(patient: PatientState) {
        _patientState.value = patient
    }
}