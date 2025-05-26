package com.example.hospitalfrontend.ui.cure.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.domain.model.medical.VitalSignState
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageListCure
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NoDataInformation
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
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
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = customPrimaryColor,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("createCure/${patientId}")
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Create a new medication"
                    )
                }
            }
        },topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id=R.string.list_care_title), style = TextStyle(
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
            if (!isLoading && cures.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(customPrimaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    NoDataInformation(
                        labelRes = R.string.empty_care,
                        infoRes = R.string.create_care,
                        icon = Icons.Filled.NoteAlt
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),

                    ) {
                    cures.forEachIndexed { index, cure ->
                        CureDetailCard(cure, navController, cures.size - index)
                    }
                }

            }
        }
    }
}

@Composable
fun CureDetailCard(cure: VitalSignState, navController: NavHostController, cureNumber: Int) {
    val customIconColor = Color(0xFF505050)
    val alertColor = Color(0xFFE74C3C)
    val defaultInfoColor = Color(0xFF7F8C8D)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(id = R.string.care)} $cureNumber",
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
                labelResId = R.string.blood_pressure,
                info = "${cure.systolicBloodPressure} mmHg/${cure.diastolicBloodPressure} mmHg",
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
                labelResId = R.string.respiratory_rate,
                info = "${cure.respiratoryRate} bpm",
                icon = Icons.Filled.MonitorHeart,
                iconColor = getRespiratoryRateColor(cure.respiratoryRate),
                infoColor = if (getRespiratoryRateColor(cure.respiratoryRate) == Color.Red) alertColor else defaultInfoColor
            )

            DetailItemWithIcon(
                labelResId = R.string.pulse,
                info = cure.pulse.toString(),
                icon = Icons.Filled.Monitor,
                iconColor = getPulseColor(cure.pulse),
                infoColor = if (getPulseColor(cure.pulse) == Color.Red) alertColor else defaultInfoColor
            )

            DetailItemWithIcon(
                labelResId = R.string.temperature,
                info = "${cure.temperature} ºC",
                icon = Icons.Filled.DeviceThermostat,
                iconColor = getTemperatureColor(cure.temperature),
                infoColor = if (getTemperatureColor(cure.temperature) == Color.Red) alertColor else defaultInfoColor
            )

            DetailItemWithIcon(
                labelResId = R.string.oxygen_saturation,
                info = "${cure.oxygenSaturation} %",
                icon = Icons.Filled.Air,
                iconColor = getOxygenSaturationColor(cure.oxygenSaturation),
                infoColor = if (getOxygenSaturationColor(cure.oxygenSaturation) == Color.Red) alertColor else defaultInfoColor
            )
        }
    }
}

@Composable
fun DetailItemWithIcon(
    @StringRes labelResId: Int,
    info: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    infoColor: Color
) {
    val label = stringResource(id = labelResId)
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