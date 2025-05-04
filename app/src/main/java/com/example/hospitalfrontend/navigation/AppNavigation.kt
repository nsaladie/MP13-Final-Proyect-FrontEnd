package com.example.hospitalfrontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.ui.splash.SplashScreen
import com.example.hospitalfrontend.data.remote.viewmodel.NurseRemoteViewModel
import com.example.hospitalfrontend.ui.login.LoginOrRegisterScreen
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel

@Composable
fun AppNavigation(
    nurseViewModel: NurseViewModel,
    remoteViewModel: NurseRemoteViewModel
) {
    val loginState by nurseViewModel.loginState.collectAsState()
    val navController = rememberNavController()
    val startDestination = if (loginState.isLogin) "home" else "login"
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppScreen.SplashScreen.route) {
            SplashScreen {
                navController.navigate(AppScreen.LoginOrRegisterScreen.route) {
                    popUpTo(AppScreen.SplashScreen.route) { inclusive = true }
                }
            }
        }
        composable(AppScreen.LoginOrRegisterScreen.route) {
            LoginOrRegisterScreen(navController, nurseViewModel, remoteViewModel)
        }

    }
}

