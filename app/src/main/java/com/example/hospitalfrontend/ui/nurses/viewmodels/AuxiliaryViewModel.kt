package com.example.hospitalfrontend.ui.nurses.viewmodels

import androidx.lifecycle.ViewModel
import com.example.hospitalfrontend.model.AuxiliaryState
import com.example.hospitalfrontend.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuxiliaryViewModel : ViewModel() {
    // Login variables
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> get() = _loginState.asStateFlow()

    private val _auxiliaryState = MutableStateFlow<AuxiliaryState?>(null)
    val auxiliaryState: StateFlow<AuxiliaryState?> get() = _auxiliaryState.asStateFlow()

    // Variable for searching a nurse
    private val _currentSearchName = MutableStateFlow("")
    val currentSearchName: StateFlow<String> get() = _currentSearchName.asStateFlow()

    // Update login state
    private fun setLoginState(isLogin: Boolean) {
        _loginState.update { currentState ->
            currentState.copy(isLogin = isLogin)
        }
    }

    fun getLoginState(): Boolean {
        return _loginState.value.isLogin
    }

    // Disconnect Nurse User
    fun disconnectAuxiliary() {
        setLoginState(false)
        _auxiliaryState.value = null
        _currentSearchName.value = ""
    }

    // If the data is on data base save the data of response in a variable
    fun loginAuxiliary(auxiliary: AuxiliaryState?) {
        setLoginState(true)
        if (auxiliary != null) {
            _auxiliaryState.value = auxiliary
        } else {
            _auxiliaryState.value = null
        }
    }


}
