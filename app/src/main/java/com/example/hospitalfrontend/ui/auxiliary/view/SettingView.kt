package com.example.hospitalfrontend.ui.configuration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    navController: NavController,
    auxiliaryViewModel: AuxiliaryViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuració") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Configuració personal")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        TODO()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}