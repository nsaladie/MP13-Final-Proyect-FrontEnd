package com.example.hospitalfrontend.navigation

sealed class AppScreen(val route: String) {
    object SplashScreen : AppScreen(route = "splash_screen")
    object LoginOrRegisterScreen : AppScreen(route = "login_screen")

}