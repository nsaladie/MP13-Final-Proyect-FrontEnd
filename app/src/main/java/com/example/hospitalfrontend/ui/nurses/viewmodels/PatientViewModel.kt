package com.example.hospitalfrontend.ui.nurses.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.model.AuxiliaryState
import com.example.hospitalfrontend.model.PatientState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PatientViewModel : ViewModel() {
    private val _patientState = MutableStateFlow<PatientState?>(null)
    val patientState: StateFlow<PatientState?> get() = _patientState.asStateFlow()

}