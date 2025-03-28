package com.example.hospitalfrontend.ui.nurses.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

sealed class Screen(val route: String) {
    data class PersonalData(val patientId: Int) : Screen("personalData/$patientId")
    data class Diagnosis(val patientId: Int) : Screen("diagnosis/$patientId")
    object ListRegister : Screen("listRegister")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController, patientId: Int
) {
    // Consistent color definition
    val customPrimaryColor = Color(0xFFA9C7C7)

    val options = listOf(
        "Dades personals del pacient" to Screen.PersonalData(patientId).route,
        "Diagnòstic d'Ingrés" to Screen.Diagnosis(patientId).route,
        "Llistat de cures" to Screen.ListRegister.route
    )

    Scaffold(
        containerColor = customPrimaryColor, topBar = {
            TopAppBar(
                title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "MENÚ PACIENT", style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.Close, contentDescription = "Close", tint = Color.Black
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = customPrimaryColor, navigationIconContentColor = Color.Black
            )
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            options.forEach { (text, route) ->
                ButtonMenuHome(
                    onClick = { navController.navigate(route) }, textButton = text
                )
            }
        }
    }
}

@Composable
fun ButtonMenuHome(
    onClick: () -> Unit, textButton: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            TextButton(
                onClick = onClick, modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = textButton, style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 20.sp,
                        color = Color(0xFF2C3E50),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}