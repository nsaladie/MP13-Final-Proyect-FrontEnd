package com.example.hospitalfrontend.ui.home.view

import androidx.compose.animation.animateContentSize
import com.example.hospitalfrontend.R
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel

sealed class Screen(val route: String) {
    data class PersonalData(val patientId: Int) : Screen("personalData/$patientId")
    data class Diagnosis(val patientId: Int) : Screen("diagnosis/$patientId")
    data class ListRegister(val patientId: Int) : Screen("listRegister/$patientId")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    patientId: Int,
    patientViewModel: PatientViewModel,
    patientRemoteViewModel: PatientRemoteViewModel
) {
    val primaryColor = Color(0xFFA9C7C7)
    val textColor = Color(0xFF2C3E50)
    val cardColor = Color(0xFFF5F7FA)
    val accentColor = Color(0xFF3498DB)
    val dischargeColor = Color(0xFFF55753)
    val personalDataText = stringResource(id = R.string.menu_personal_data)
    val diagnosisText = stringResource(id = R.string.menu_diagnosis)
    val careListText = stringResource(id = R.string.menu_care_list)
    val dischargeText = stringResource(id = R.string.menu_discharge_patient)

    val menuOptions = listOf(
        Triple(
            personalDataText,
            Screen.PersonalData(patientId).route,
            Icons.Outlined.Person
        ), Triple(
            diagnosisText, Screen.Diagnosis(patientId).route, Icons.Outlined.Description
        ), Triple(
            careListText, Screen.ListRegister(patientId).route, Icons.Outlined.MedicalServices
        )
    )

    val dischargeOption = Triple(
        dischargeText,
        Screen.ListRegister(patientId).route,
        Icons.Outlined.DeleteForever
    )

    val remote = PatientRemoteViewModel()
    var patientState by remember { mutableStateOf<PatientState?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDischargeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        remote.getPatientById(patientId, patientViewModel)
        patientViewModel.patientState.collect { newState ->
            patientState = newState
            isLoading = false
        }
    }

    if (showDischargeDialog) {
        AlertDialog(
            onDismissRequest = { showDischargeDialog = false },
            title = {
                Text(
                    stringResource(id = R.string.alert_dialog_discharge_title), style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
            },
            text = {
                Text(
                    stringResource(id = R.string.alert_dialog_discharge_text), style = TextStyle(
                        fontFamily = LatoFontFamily, fontSize = 18.sp
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        patientState?.let { patientRemoteViewModel.updatePatientDischarge(it) }
                        showDischargeDialog = false
                        navController.popBackStack()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = dischargeColor
                    )
                ) {
                    Text(stringResource(R.string.alert_dialog_discharge_ok))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDischargeDialog = false }) {
                    Text(stringResource(R.string.alert_dialog_discharge_cancel))
                }
            },
            containerColor = Color.White,
            titleContentColor = textColor,
            textContentColor = textColor
        )
    }

    Scaffold(
        containerColor = primaryColor, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.menu_title), style = TextStyle(
                            fontSize = 30.sp,
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
                            Spacer(modifier = Modifier.height(130.dp))

                            DischargeMenuButton(
                                onClick = { showDischargeDialog = true },
                                text = dischargeOption.first,
                                icon = dischargeOption.third,
                                textColor = Color.White,
                                cardColor = dischargeColor
                            )


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

@Composable
fun DischargeMenuButton(
    onClick: () -> Unit, text: String, icon: ImageVector, textColor: Color, cardColor: Color
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
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(8.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = text,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = text, style = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontSize = 20.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = "Navegar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}