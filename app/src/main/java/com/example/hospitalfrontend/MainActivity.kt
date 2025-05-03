package com.example.hospitalfrontend

import PersonalData
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.network.*
import com.example.hospitalfrontend.ui.login.LoginScreenAuxiliary
import com.example.hospitalfrontend.ui.nurses.view.*
import com.example.hospitalfrontend.ui.nurses.viewmodels.*
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HospitalFrontEndTheme {
                MyAppHomePage(
                    auxiliaryViewModel = AuxiliaryViewModel(),
                    auxiliaryRemoteViewModel = AuxiliaryRemoteViewModel(),
                    patientRemoteViewModel = PatientRemoteViewModel(),
                    patientViewModel = PatientViewModel(),
                    diagnosisViewModel = DiagnosisViewModel(),
                    diagnosisRemoteViewModel = DiagnosisRemoteViewModel(),
                    patientState = PatientState()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePage() {
    HospitalFrontEndTheme {
        MyAppHomePage(
            auxiliaryViewModel = AuxiliaryViewModel(),
            auxiliaryRemoteViewModel = AuxiliaryRemoteViewModel(),
            patientRemoteViewModel = PatientRemoteViewModel(),
            patientViewModel = PatientViewModel(),
            diagnosisViewModel = DiagnosisViewModel(),
            diagnosisRemoteViewModel = DiagnosisRemoteViewModel(),
            patientState = PatientState()
        )
    }
}

@Composable
fun MyAppHomePage(
    auxiliaryViewModel: AuxiliaryViewModel,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    patientState: PatientState
) {
    val remoteApiMessageListRoom = patientRemoteViewModel.remoteApiListMessageRoom.value
    val navController = rememberNavController()

    // Get the state of login
    val loginState by auxiliaryViewModel.loginState.collectAsState()

    // Determine the initial screen
    val startDestination = if (loginState.isLogin) "home" else "login"

    LaunchedEffect(loginState.isLogin) {
        if (!loginState.isLogin) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreenAuxiliary(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                auxiliaryRemoteViewModel = auxiliaryRemoteViewModel
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

        composable("home") {
            var isError = remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                patientRemoteViewModel.getAllRooms()
            }
            when (remoteApiMessageListRoom) {
                is RemoteApiMessageListRoom.Success -> {
                    if (remoteApiMessageListRoom.message.isEmpty()) {
                        isError.value = true
                    } else {
                        patientViewModel.loadRooms(remoteApiMessageListRoom.message)
                    }
                }

                is RemoteApiMessageListRoom.Error -> {
                    isError.value = true
                    Log.d("Error List", "Error loading list of rooms")
                }

                is RemoteApiMessageListRoom.Loading -> {
                }
            }
            HomeScreen(
                navController = navController,
                patientViewModel = patientViewModel,
                isError = isError,
            )
        }

        composable(
            "menu/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            MenuScreen(
                navController = navController,
                patientId = patientId,
                patientViewModel = patientViewModel
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

    }
}