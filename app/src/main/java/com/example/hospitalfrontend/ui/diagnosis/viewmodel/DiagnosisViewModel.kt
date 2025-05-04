package com.example.hospitalfrontend.ui.diagnosis.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DiagnosisViewModel : ViewModel() {
    private val _diagnosisDetail = MutableStateFlow<DiagnosisState?>(null)
    val diagnosisDetail: StateFlow<DiagnosisState?> get() = _diagnosisDetail.asStateFlow()

    fun setDiagnosisDetail(diagnosis: DiagnosisState?) {
        _diagnosisDetail.value = diagnosis
    }
}