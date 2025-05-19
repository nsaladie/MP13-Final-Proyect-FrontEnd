package com.example.hospitalfrontend.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Routes of navigation
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Inici", Icons.Default.Home)
    object Medication : BottomNavItem("medication", "Medicaments", Icons.Default.Medication)
    object Configuration : BottomNavItem("configuration", "Configuració", Icons.Default.Settings)
}