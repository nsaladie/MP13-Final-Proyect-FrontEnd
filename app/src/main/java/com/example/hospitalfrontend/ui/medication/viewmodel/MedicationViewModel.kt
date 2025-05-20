package com.example.hospitalfrontend.ui.medication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import kotlinx.coroutines.flow.MutableStateFlow

class MedicationViewModel : ViewModel(
) {
    private val _listMedication = MutableStateFlow<List<MedicationState>>(listOf())
    val listMedication: MutableStateFlow<List<MedicationState>> = _listMedication

    fun loadListMedication(medication: List<MedicationState>) {
        _listMedication.value = medication
    }
}