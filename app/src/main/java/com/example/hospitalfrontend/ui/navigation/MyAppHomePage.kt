package com.example.hospitalfrontend.ui.navigation

import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.data.remote.viewmodel.*
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hospitalfrontend.ui.components.BottomNavigationBar
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.diagnosis.viewmodel.DiagnosisViewModel
import com.example.hospitalfrontend.ui.medication.viewmodel.MedicationViewModel
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppHomePage() {
    val navController = rememberNavController()

    val auxiliaryViewModel = remember { AuxiliaryViewModel() }
    val auxiliaryRemoteViewModel = remember { AuxiliaryRemoteViewModel() }
    val patientRemoteViewModel = remember { PatientRemoteViewModel() }
    val patientViewModel = remember { PatientViewModel() }
    val diagnosisViewModel = remember { DiagnosisViewModel() }
    val diagnosisRemoteViewModel = remember { DiagnosisRemoteViewModel() }
    val medicationViewModel = remember { MedicationViewModel() }
    val medicationRemoteViewModel = remember { MedicationRemoteViewModel() }

    val loginState by auxiliaryViewModel.loginState.collectAsState()
    val startDestination = if (loginState.isLogin) "home" else "login"

    LaunchedEffect(loginState.isLogin) {
        if (!loginState.isLogin) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != null && currentRoute != "login"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        },
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        bottom = paddingValues.calculateBottomPadding(),
                        top = 0.dp,
                        start = 0.dp,
                        end = 0.dp
                    )
                )
        ) {
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                auxiliaryViewModel = auxiliaryViewModel,
                auxiliaryRemoteViewModel = auxiliaryRemoteViewModel,
                patientRemoteViewModel = patientRemoteViewModel,
                patientViewModel = patientViewModel,
                diagnosisViewModel = diagnosisViewModel,
                diagnosisRemoteViewModel = diagnosisRemoteViewModel,
                medicationViewModel = medicationViewModel,
                medicationRemoteViewModel = medicationRemoteViewModel
            )
        }
    }
}