package com.example.hospitalfrontend.ui.nurses.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.model.DiagnosisState
import kotlinx.coroutines.flow.*

class DiagnosisViewModel : ViewModel() {
    private val _diagnosisDetail = MutableStateFlow<DiagnosisState?>(null)
    val diagnosisDetail: StateFlow<DiagnosisState?> get() = _diagnosisDetail.asStateFlow()

    fun setDiagnosisDetail(diagnosis: DiagnosisState) {
        _diagnosisDetail.value = diagnosis
    }
}