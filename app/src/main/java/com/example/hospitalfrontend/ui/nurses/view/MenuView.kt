package com.example.hospitalfrontend.ui.nurses.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.network.PatientRemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel

sealed class Screen(val route: String) {
    data class PersonalData(val patientId: Int) : Screen("personalData/$patientId")
    data class Diagnosis(val patientId: Int) : Screen("diagnosis/$patientId")
    data class ListRegister(val patientId: Int) : Screen("listRegister/$patientId")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController, patientId: Int, patientViewModel: PatientViewModel
) {
    val primaryColor = Color(0xFFA9C7C7)
    val textColor = Color(0xFF2C3E50)
    val cardColor = Color(0xFFF5F7FA)
    val accentColor = Color(0xFF3498DB)

    val menuOptions = listOf(
        Triple(
            "Dades personals del pacient",
            Screen.PersonalData(patientId).route,
            Icons.Outlined.Person
        ), Triple(
            "Diagnòstic d'Ingrés", Screen.Diagnosis(patientId).route, Icons.Outlined.Description
        ), Triple(
            "Llistat de cures", Screen.ListRegister(patientId).route, Icons.Outlined.MedicalServices
        )
    )

    val remote = PatientRemoteViewModel()
    var patientState by remember { mutableStateOf<PatientState?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(patientViewModel.patientState) {
        remote.getPatientById(patientId, patientViewModel)
        patientViewModel.patientState.collect { newState ->
            patientState = newState
            isLoading = false
        }
    }

    Scaffold(
        containerColor = primaryColor, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MENÚ PACIENT", style = TextStyle(
                            fontSize = 26.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            Icons.Filled.Close, contentDescription = "Tornar", tint = Color.Black
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor, navigationIconContentColor = Color.Black
                )
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(16.dp), color = Color.White
                    )
                } else {
                    patientState?.let { patient ->
                        EnhancedPatientInfoCard(
                            patientName = "${patient.name} ${patient.surname}",
                            textColor = textColor,
                            historialNumber = patient.historialNumber,
                            cardColor = cardColor,
                            accentColor = accentColor
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Menu Options
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ), verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            menuOptions.forEach { (text, route, icon) ->
                                AnimatedMenuButton(
                                    onClick = { navController.navigate(route) },
                                    text = text,
                                    icon = icon,
                                    textColor = textColor,
                                    cardColor = cardColor,
                                    accentColor = accentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedPatientInfoCard(
    patientName: String,
    textColor: Color,
    historialNumber: Int,
    cardColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Patient Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Pacient",
                        modifier = Modifier.size(40.dp),
                        tint = accentColor
                    )
                }
                // Patient Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = patientName, style = TextStyle(
                            fontSize = 22.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        ), maxLines = 1, overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NHC:", style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = historialNumber.toString(), style = TextStyle(
                                fontSize = 20.sp, fontFamily = NunitoFontFamily, color = textColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedMenuButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    textColor: Color,
    cardColor: Color,
    accentColor: Color
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = if (isPressed) 2.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            ), colors = CardDefaults.cardColors(
            containerColor = cardColor
        ), shape = RoundedCornerShape(20.dp)
    ) {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = cardColor
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(16.dp),
            elevation = null
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = text,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = text, style = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontSize = 20.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = "Navegar",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )

            }
        }
    }
}