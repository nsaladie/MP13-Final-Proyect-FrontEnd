package com.example.hospitalfrontend.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListRoom
import com.example.hospitalfrontend.data.remote.viewmodel.*
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.cure.view.*
import com.example.hospitalfrontend.ui.diagnosis.view.*
import com.example.hospitalfrontend.ui.diagnosis.viewmodel.DiagnosisViewModel
import com.example.hospitalfrontend.ui.home.view.HomeScreen
import com.example.hospitalfrontend.ui.login.LoginScreenAuxiliary
import com.example.hospitalfrontend.ui.medication.view.MedicationScreen
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
import PersonalData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hospitalfrontend.ui.auxiliary.view.SettingsScreen
import com.example.hospitalfrontend.ui.home.view.MenuScreen
import com.example.hospitalfrontend.ui.medication.view.UpdateMedicationScreen
import com.example.hospitalfrontend.ui.medication.view.CreateMedicationScreen
import com.example.hospitalfrontend.ui.medication.viewmodel.MedicationViewModel
import com.example.hospitalfrontend.ui.patients.view.AssignExistingPatientView
import com.example.hospitalfrontend.ui.patients.view.CreatePatientData
import com.example.hospitalfrontend.ui.patients.view.PatientRoomAssignmentView
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientSharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    auxiliaryViewModel: AuxiliaryViewModel,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    medicationViewModel: MedicationViewModel,
    medicationRemoteViewModel: MedicationRemoteViewModel
) {
    val sharedViewModel: PatientSharedViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            LoginScreenAuxiliary(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                auxiliaryRemoteViewModel = auxiliaryRemoteViewModel
            )
        }

        // Principal view with bottom navigation
        composable(BottomNavItem.Home.route) {
            var isError = remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                patientRemoteViewModel.getAllRooms()
            }
            when (patientRemoteViewModel.remoteApiListMessageRoom.value) {
                is RemoteApiMessageListRoom.Success -> {
                    val message =
                        (patientRemoteViewModel.remoteApiListMessageRoom.value as RemoteApiMessageListRoom.Success).message
                    if (message.isEmpty()) {
                        isError.value = true
                    } else {
                        patientViewModel.loadRooms(message)
                        sharedViewModel.updateIdsFromRooms(message)
                    }
                }

                is RemoteApiMessageListRoom.Error -> {
                    isError.value = true
                    Log.d("Error List", "Error loading list of rooms")
                }

                is RemoteApiMessageListRoom.Loading -> {}
            }
            HomeScreen(
                navController = navController,
                patientViewModel = patientViewModel,
                isError = isError,
                sharedViewModel = sharedViewModel
            )
        }

        composable(BottomNavItem.Medication.route) {
            MedicationScreen(navController, medicationViewModel, medicationRemoteViewModel)
        }

        composable(BottomNavItem.Configuration.route) {
            SettingsScreen(navController, auxiliaryViewModel)
        }

        // Rest of views of the applications
        composable(
            "createMedication",
        ) {
            CreateMedicationScreen(
                navController = navController,
                medicationRemoteViewModel = medicationRemoteViewModel,
            )
        }

        composable(
            "diagnosis/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            DiagnosisScreen(
                navController = navController,
                diagnosisViewModel = diagnosisViewModel,
                diagnosisRemoteViewModel = diagnosisRemoteViewModel,
                patientId = patientId
            )
        }

        composable(
            "medicationUpdate/{medicationId}",
            arguments = listOf(navArgument("medicationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicationId = backStackEntry.arguments?.getInt("medicationId") ?: -1
            UpdateMedicationScreen(
                navController = navController,
                medicationId = medicationId,
                medicationRemoteViewModel = medicationRemoteViewModel
            )
        }

        composable(
            "diagnosis/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            DiagnosisScreen(
                navController = navController,
                diagnosisViewModel = diagnosisViewModel,
                diagnosisRemoteViewModel = diagnosisRemoteViewModel,
                patientId = patientId
            )
        }

        composable(
            "createDiagnosis/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            val isError = remember { mutableStateOf(false) }

            CreateDiagnosisScreen(
                navController = navController,
                diagnosisRemoteViewModel = diagnosisRemoteViewModel,
                patientId = patientId,
                isError = isError,
                auxiliaryViewModel = auxiliaryViewModel
            )
        }

        composable(
            "diagnosisHistory/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1

            DiagnosisHistoryScreen(
                navController = navController,
                diagnosisRemoteViewModel = diagnosisRemoteViewModel,
                patientId = patientId,
                diagnosisViewModel = diagnosisViewModel,
            )
        }

        composable("listRegister/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")?.toIntOrNull()

            if (patientId != null) {
                ListCuresScreen(
                    navController = navController,
                    patientRemoteViewModel = patientRemoteViewModel,
                    patientViewModel = patientViewModel,
                    patientId = patientId
                )
            }
        }

        composable(
            "cureDetail/{vitalSignId}",
            arguments = listOf(navArgument("vitalSignId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vitalSignId = backStackEntry.arguments?.getInt("vitalSignId") ?: -1
            CureDetailsScreen(navController, patientRemoteViewModel, vitalSignId)
        }

        composable(
            "menu/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            MenuScreen(
                navController = navController,
                patientId = patientId,
                patientViewModel = patientViewModel,
                patientRemoteViewModel = patientRemoteViewModel
            )
        }

        composable(
            "personalData/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            PersonalData(
                navController = navController,
                patientRemoteViewModel = patientRemoteViewModel,
                patientViewModel = patientViewModel,
                patientId = patientId
            )
        }

        composable(
            "createCure/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1

            CreateCureScreen(
                navController = navController,
                patientRemoteViewModel = patientRemoteViewModel,
                patientId = patientId,
                auxiliaryViewModel = auxiliaryViewModel,
            )
        }

        composable(
            "assignPatient/{roomId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            PatientRoomAssignmentView(
                navController = navController,
                patientRemoteViewModel = patientRemoteViewModel,
                patientId = patientId,
                patientViewModel = patientViewModel,
                roomId = roomId,
                idsAsignados = emptyList(),
                sharedViewModel = sharedViewModel
            )
        }

        composable(
            "searchPatient/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = (backStackEntry.arguments?.getString("roomId") ?: -1).toString()
            AssignExistingPatientView(
                navController = navController,
                patientRemoteViewModel = patientRemoteViewModel,
                patientViewModel = patientViewModel,
                roomId = roomId,
                sharedViewModel = sharedViewModel
            )
        }

        composable(
            "createPatient/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = (backStackEntry.arguments?.getString("roomId") ?: -1).toString()
            CreatePatientData(
                navController = navController,
                patientRemoteViewModel = patientRemoteViewModel,
                patientViewModel = patientViewModel,
                patientId = -1,
                roomId = roomId
            )
        }
    }
}