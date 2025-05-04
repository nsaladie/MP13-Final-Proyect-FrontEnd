package com.example.hospitalfrontend.ui.nurses.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hospitalfrontend.domain.model.auth.LoginState
import com.example.hospitalfrontend.domain.model.user.NurseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NurseViewModel : ViewModel() {
    // Login variables
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> get() = _loginState.asStateFlow()

    private val _nurseState = MutableStateFlow<NurseState?>(null)
    val nurseState: StateFlow<NurseState?> get() = _nurseState.asStateFlow()

    // Variable for a list of nurse
    private val _nurses = MutableStateFlow<List<NurseState>>(listOf())
    val nurses: StateFlow<List<NurseState>> = _nurses

    // List of nurse specialties
    private val _specialityNurse = MutableStateFlow<List<String>>(emptyList())
    val specialityNurse: StateFlow<List<String>> = _specialityNurse

    // Variable for search nurse
    private val _currentSearchName = MutableStateFlow("")
    val currentSearchName: StateFlow<String> get() = _currentSearchName.asStateFlow()

    init {
        loadSpeciality()
    }

    private fun loadSpeciality() {
        viewModelScope.launch {
            val sortedSpecialities = listOf(
                "Pediatrics Nursing",
                "Cardiology Nursing",
                "Neurology Nursing",
                "Oncology Nursing",
                "Surgical Nursing",
                "Neonatal Nursing",
                "Emergency Nursing",
                "Critical Care Nursing",
                "Psychiatric Nursing",
                "Obstetrics and Gynecology Nursing",
                "Orthopedic Nursing",
                "Anesthesia Nursing",
                "Palliative Care Nursing",
                "Nephrology Nursing",
                "Transplant Nursing",
                "Forensic Nursing",
                "Research Nursing"
            ).sorted()
            _specialityNurse.value = sortedSpecialities
        }
    }

    // Load the list of the Nurse
    fun loadNurses(nurse: List<NurseState>) {
        _nurses.value = nurse
    }

    // Update the value of login
    private fun setLoginState(isLogin: Boolean) {
        _loginState.update { currentState ->
            currentState.copy(isLogin = isLogin)
        }
    }

    fun getLoginState(): Boolean {
        return _loginState.value.isLogin
    }

    fun getNurseState(): NurseState? {
        return _nurseState.value
    }

    // Disconnect Nurse User
    fun disconnectNurse() {
        setLoginState(false)
        _nurseState.value = null
        _currentSearchName.value = ""
    }

    // Add new Nurse into the list
    fun addNurse(nurse: NurseState) {
        _nurseState.value = nurse
        setLoginState(true)
    }

    // If the data is on data base save the data of response in a variable
    fun loginNurse(nurse: NurseState) {
        setLoginState(true)
        _nurseState.value = nurse
    }

    fun updateCurrentSearchName(name: String) {
        _currentSearchName.value = name
    }

    fun deleteNurse() {
        disconnectNurse()
    }
}
