package com.example.hospitalfrontend.ui.nurses.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.model.VitalSignState
import com.example.hospitalfrontend.network.PatientRemoteViewModel
import com.example.hospitalfrontend.network.RemoteApiMessageListCure
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import com.example.hospitalfrontend.utils.getBloodPressureColor
import com.example.hospitalfrontend.utils.getOxygenSaturationColor
import com.example.hospitalfrontend.utils.getPulseColor
import com.example.hospitalfrontend.utils.getRespiratoryRateColor
import com.example.hospitalfrontend.utils.getTemperatureColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCuresScreen(
    navController: NavHostController,
    patientViewModel: PatientViewModel,
    patientRemoteViewModel: PatientRemoteViewModel,
    isError: MutableState<Boolean>,
    patientId: Int
) {
    val customPrimaryColor = Color(0xFFA9C7C7)
    var isLoading by remember { mutableStateOf(true) }
    val cures by patientViewModel.cures.collectAsState()
    val remoteApiMessageListCure = patientRemoteViewModel.remoteApiListMessageCure.value

    LaunchedEffect(Unit) {
        patientRemoteViewModel.getAllCures(patientId)
    }

    LaunchedEffect(remoteApiMessageListCure) {
        when (remoteApiMessageListCure) {
            is RemoteApiMessageListCure.Success -> {
                patientViewModel.loadCures(remoteApiMessageListCure.message)
                isLoading = false
            }

            is RemoteApiMessageListCure.Loading -> {
                isLoading = true
            }

            is RemoteApiMessageListCure.Error -> {
                isError.value = true
                isLoading = false
            }
        }
    }

    if (isError.value) {
        AlertDialog(onDismissRequest = { isError.value = false }, confirmButton = {
            TextButton(onClick = { isError.value = false }) {
                Text("OK")
            }
        }, title = {
            Text(
                text = "Error: List Cures", color = Color.Red, style = TextStyle(
                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = "Failed to fetch cure data", style = TextStyle(
                    fontFamily = LatoFontFamily
                )
            )
        })
    }

    Scaffold(
        containerColor = customPrimaryColor, topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LLISTAT DE CURES", style = TextStyle(
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
                    containerColor = customPrimaryColor, scrolledContainerColor = customPrimaryColor
                ), actions = {
                    IconButton(onClick = { navController.navigate("createCure/${patientId}") }) {
                        Icon(
                            Icons.Filled.MedicalServices,
                            contentDescription = "Cures",
                            tint = Color.Black,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                })
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor),
            contentAlignment = Alignment.Center

        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White, modifier = Modifier.size(50.dp)
                    )
                }

            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),

                ) {
                if (cures.isEmpty()) {
                    Text(
                        "No hi ha cures disponibles",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    cures.forEach { cure ->
                        CureDetailCard(cure, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun CureDetailCard(cure: VitalSignState, navController: NavHostController) {
    val customIconColor = Color(0xFF505050)
    val alertColor = Color(0xFFE74C3C)
    val defaultInfoColor = Color(0xFF7F8C8D)

    fun hasVitalSignAlert(): Boolean {
        return getBloodPressureColor(
            cure.systolicBloodPressure, cure.diastolicBloodPressure
        ) == alertColor || getRespiratoryRateColor(cure.respiratoryRate) == alertColor || getPulseColor(
            cure.pulse
        ) == alertColor || getTemperatureColor(cure.temperature) == alertColor || getOxygenSaturationColor(
            cure.oxygenSaturation
        ) == alertColor
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (cure.id != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cura ${cure.id}",
                        style = TextStyle(
                            fontFamily = NunitoFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    TextButton(
                        onClick = {

                            navController.navigate("cureDetail/${cure.id}")
                        }, colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Info", color = customIconColor, style = TextStyle(
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Veure detalls",
                                tint = customIconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                DetailItemWithIcon(
                    label = "Tensió Arterial",
                    info = "${cure.systolicBloodPressure}mmHg/${cure.diastolicBloodPressure}mmHg",
                    icon = Icons.Filled.Favorite,
                    iconColor = getBloodPressureColor(
                        cure.systolicBloodPressure, cure.diastolicBloodPressure
                    ),
                    infoColor = if (getBloodPressureColor(
                            cure.systolicBloodPressure, cure.diastolicBloodPressure
                        ) == Color.Red
                    ) alertColor else defaultInfoColor
                )

                DetailItemWithIcon(
                    label = "Freqüència Respiratòria",
                    info = cure.respiratoryRate.toString(),
                    icon = Icons.Filled.MonitorHeart,
                    iconColor = getRespiratoryRateColor(cure.respiratoryRate),
                    infoColor = if (getRespiratoryRateColor(cure.respiratoryRate) == Color.Red) alertColor else defaultInfoColor
                )

                DetailItemWithIcon(
                    label = "Pols",
                    info = cure.pulse.toString(),
                    icon = Icons.Filled.Monitor,
                    iconColor = getPulseColor(cure.pulse),
                    infoColor = if (getPulseColor(cure.pulse) == Color.Red) alertColor else defaultInfoColor
                )

                DetailItemWithIcon(
                    label = "Temperatura",
                    info = "${cure.temperature}ºC",
                    icon = Icons.Filled.DeviceThermostat,
                    iconColor = getTemperatureColor(cure.temperature),
                    infoColor = if (getTemperatureColor(cure.temperature) == Color.Red) alertColor else defaultInfoColor
                )

                DetailItemWithIcon(
                    label = "Saturació d'Oxigen",
                    info = "${cure.oxygenSaturation}%",
                    icon = Icons.Filled.Air,
                    iconColor = getOxygenSaturationColor(cure.oxygenSaturation),
                    infoColor = if (getOxygenSaturationColor(cure.oxygenSaturation) == Color.Red) alertColor else defaultInfoColor
                )
            } else {
                Text(
                    text = "No hi ha cures disponibles",
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DetailItemWithIcon(
    label: String,
    info: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    infoColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label, style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )
            Text(
                text = info, style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = 18.sp, color = infoColor
                )
            )
        }
    }
}