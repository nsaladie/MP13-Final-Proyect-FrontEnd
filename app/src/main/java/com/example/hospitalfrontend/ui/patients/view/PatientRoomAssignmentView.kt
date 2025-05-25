package com.example.hospitalfrontend.ui.patients.view

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientSharedViewModel
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel

sealed class Screen(val route: String) {
    data class AssignPatient(val roomId: String) : Screen("searchPatient/$roomId")
    data class ListRegister(val roomId: String) : Screen("createPatient/$roomId")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRoomAssignmentView(
    navController: NavController,
    patientId: Int,
    patientViewModel: PatientViewModel,
    patientRemoteViewModel: PatientRemoteViewModel,
    roomId: String,
    idsAsignados: List<Int>,
    sharedViewModel: PatientSharedViewModel
) {
    val primaryColor = Color(0xFFA9C7C7)
    val textColor = Color(0xFF2C3E50)
    val cardColor = Color(0xFFF5F7FA)
    val accentColor = Color(0xFF3498DB)
    val searchPatientText = stringResource(id = R.string.menu_assign_searchPatient)
    val createPatientText = stringResource(id = R.string.menu_assign_createPatient)

    val menuOptions = listOf(
        Triple(searchPatientText, Screen.AssignPatient(roomId).route, Icons.Outlined.Person),
        Triple(createPatientText, Screen.ListRegister(roomId).route, Icons.Outlined.ControlPoint)
    )

    LaunchedEffect(Unit) {
        patientRemoteViewModel.clearApiMessage()
    }

    LaunchedEffect(patientViewModel.patientState) {
        patientViewModel.resetPatientData()
    }

    Scaffold(
        containerColor = primaryColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.menu_assign_title),
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Tornar", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                menuOptions.forEach { (text, route, icon) ->
                    AnimatedMenuButton(
                        onClick = {
                            navController.navigate(route)
                        },
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