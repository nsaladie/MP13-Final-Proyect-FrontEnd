package com.example.hospitalfrontend.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.hospitalfrontend.ui.navigation.BottomNavItem

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Medication,
        BottomNavItem.Configuration
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // List of principals routes
    val bottomNavRoutes = items.map { it.route }

    val isInSubRoute = currentRoute != null && !bottomNavRoutes.contains(currentRoute)

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(id = item.titleResId)
                    )
                },
                label = { Text(stringResource(id = item.titleResId)) },
                selected = currentRoute == item.route ||
                        (item.route == "home" && currentRoute?.startsWith("home") == true),
                onClick = {
                    if (isInSubRoute) {
                        // If is a sub-route, clean all navigation
                        navController.navigate(item.route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}