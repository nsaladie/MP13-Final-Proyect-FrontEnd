package com.example.hospitalfrontend.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.hospitalfrontend.R

// Routes of navigation
sealed class BottomNavItem(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", R.string.bottom_bar_home, Icons.Default.Home)

    object Medication :
        BottomNavItem("medication", R.string.bottom_bar_medication, Icons.Default.Medication)

    object Configuration :
        BottomNavItem("configuration", R.string.bottom_bar_configuration, Icons.Default.Settings)
}