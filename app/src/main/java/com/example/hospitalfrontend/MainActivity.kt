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
                    remoteViewModel = NurseRemoteViewModel(),
                    auxiliaryRemoteViewModel = AuxiliaryRemoteViewModel(),
                    patientRemoteViewModel = PatientRemoteViewModel(),
                    patientViewModel = PatientViewModel(),
                    diagnosisViewModel = DiagnosisViewModel(),
                    diagnosisRemoteViewModel = DiagnosisRemoteViewModel()
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
            remoteViewModel = NurseRemoteViewModel(),
            auxiliaryRemoteViewModel = AuxiliaryRemoteViewModel(),
            patientRemoteViewModel = PatientRemoteViewModel(),
            patientViewModel = PatientViewModel(),
            diagnosisViewModel = DiagnosisViewModel(),
            diagnosisRemoteViewModel = DiagnosisRemoteViewModel()
        )
    }
}

@Composable
fun MyAppHomePage(
    auxiliaryViewModel: AuxiliaryViewModel,
    remoteViewModel: NurseRemoteViewModel,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel
) {
    val remoteApiMessageListRoom = patientRemoteViewModel.remoteApiListMessage.value
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

        composable("listCures") {
            ListCuresScreen(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                remoteViewModel = remoteViewModel
            )
        }

        composable("home") {
            val isError = remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                patientRemoteViewModel.getAllRooms()
            }
            when (remoteApiMessageListRoom) {
                is RemoteApiMessageListRoom.Success -> {
                    patientViewModel.loadRooms(remoteApiMessageListRoom.message)
                }

                is RemoteApiMessageListRoom.Error -> {
                    Log.d("ListRoom", "Error")
                }

                is RemoteApiMessageListRoom.Loading -> {
                    Log.d("List", "Loading List")
                }
            }
            HomeScreen(
                navController = navController,
                //patientRemoteViewModel = patientRemoteViewModel,
                patientViewModel = patientViewModel,
                isError = isError,
            )
        }

        composable(
            "menu/{patientId}",
            arguments = listOf(navArgument("patientId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: -1
            MenuScreen(navController = navController, patientId = patientId)
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

    }
}
