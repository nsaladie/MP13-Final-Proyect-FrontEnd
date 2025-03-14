package com.example.hospitalfrontend

import PersonalData
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.network.RemoteViewModel
import com.example.hospitalfrontend.ui.login.LoginScreenAuxiliary
import com.example.hospitalfrontend.ui.nurses.view.DiagnosisAdmission
import com.example.hospitalfrontend.ui.nurses.view.HomeScreen
import com.example.hospitalfrontend.ui.nurses.view.ListCuresScreen
import com.example.hospitalfrontend.ui.nurses.viewmodels.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HospitalFrontEndTheme {
                MyAppHomePage(
                    auxiliaryViewModel = AuxiliaryViewModel(),
                    remoteViewModel = RemoteViewModel()
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
            remoteViewModel = RemoteViewModel()
        )
    }
}

@Composable
fun MyAppHomePage(
    auxiliaryViewModel: AuxiliaryViewModel,
    remoteViewModel: RemoteViewModel
) {
    val navController = rememberNavController()

    // Observar el estado de login de manera segura
    val loginState by auxiliaryViewModel.loginState.collectAsState()

    // Determinar la pantalla inicial
    val startDestination = if (loginState.isLogin) "home" else "login"

    LaunchedEffect(loginState.isLogin) {
        Log.d("LoginState", "Estado de login: ${loginState.isLogin}")
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
                remoteViewModel = remoteViewModel
            )
        }
        composable("diagnosis") {
            DiagnosisAdmission(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                remoteViewModel = remoteViewModel
            )
        }
        composable("listCures") {
            ListCuresScreen(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                remoteViewModel = remoteViewModel
            )
        }
        composable("personalData") {
            PersonalData(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                remoteViewModel = remoteViewModel
            )
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
    }
}
