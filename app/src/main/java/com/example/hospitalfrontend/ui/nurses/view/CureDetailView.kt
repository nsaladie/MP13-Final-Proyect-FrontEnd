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
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.model.*
import com.example.hospitalfrontend.network.*
import com.example.hospitalfrontend.utils.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CureDetailsScreen(
    navController: NavHostController,
    patientRemoteViewModel: PatientRemoteViewModel,
    vitalSignId: Int
) {
    val alertColor = Color(0xFFE74C3C)
    val defaultInfoColor = Color(0xFF7F8C8D)
    val customPrimaryColor = Color(0xFFA9C7C7)

    val cureDetailState = patientRemoteViewModel.remoteApiCureDetail.value

    LaunchedEffect(vitalSignId) {
        patientRemoteViewModel.getCureDetail(vitalSignId)
    }

    when (cureDetailState) {
        is RemoteApiMessageCureDetail.Loading -> {
            LoadingScreen(customPrimaryColor)
        }

        is RemoteApiMessageCureDetail.Success -> {
            SuccessScreen(
                navController,
                cureDetailState.data,
                customPrimaryColor,
                alertColor,
                defaultInfoColor
            )
        }

        is RemoteApiMessageCureDetail.Error -> {
            ErrorScreen(customPrimaryColor) {
                patientRemoteViewModel.getCureDetail(vitalSignId)
            }
        }
    }
}

@Composable
fun LoadingScreen(customPrimaryColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customPrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White, modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun ErrorScreen(customPrimaryColor: Color, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customPrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Filled.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Error al carregar les dades",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onRetry, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, contentColor = customPrimaryColor
                )
            ) {
                Text("Reintentar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(
    navController: NavHostController,
    register: RegisterState,
    customPrimaryColor: Color,
    alertColor: Color,
    defaultInfoColor: Color
) {
    Scaffold(
        containerColor = customPrimaryColor, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DETALLS DE LA CURA", style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customPrimaryColor, scrolledContainerColor = customPrimaryColor
                ), navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.Close, contentDescription = "Close", tint = Color.Black
                        )
                    }
                })
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                BasicInfoCard(register)
                VitalSignCard(register.vitalSign, alertColor, defaultInfoColor)

                // Only show cards if they have data
                register.diet?.let { diet ->
                    if (hasData(diet)) {
                        DietCard(diet)
                    }
                }
                register.drain?.let { drain ->
                    if (!drain.output.isNullOrBlank() || !drain.type.isNullOrBlank()) {
                        DrainCard(drain)
                    }
                }
                register.hygieneType?.let { hygiene ->
                    if (!hygiene.description.isNullOrBlank()) {
                        HygieneCard(hygiene)
                    }
                }
                register.mobilization?.let { mobilization ->
                    if (hasData(mobilization)) {
                        MobilizationCard(mobilization)
                    }
                }
                register.observation?.let { observation ->
                    if (!observation.isNullOrBlank()) {
                        ObservationCard(observation)
                    }
                }
            }
        }
    }
}

// Function to check if MobilizationState has data
private fun hasData(mobilization: MobilizationState): Boolean {
    return !mobilization.sedestation.toString().isNullOrBlank() || !mobilization.walkingAssis.toString()
        .isNullOrBlank() || !mobilization.decubitus.isNullOrBlank() || !mobilization.assisDesc.isNullOrBlank()
}

// Function to check if DietState has data
private fun hasData(diet: DietState): Boolean {
    return !diet.takeData.isNullOrBlank() || diet.dietTypeTexture != null || !diet.dietTypes.isNullOrEmpty()
}

@Composable
fun BasicInfoCard(data: RegisterState) {
    CardContent(title = "${data.patient.name} ${data.patient.surname}") {
        DetailItemWithIcon(
            label = "Data",
            info = data.date!!.formatDate("dd/MM/yyyy HH:mm"),
            icon = Icons.Filled.CalendarMonth,
            iconColor = Color(0xFF505050)
        )
        DetailItemWithIcon(
            label = "Auxiliar",
            info = "${data.auxiliary.name} ${data.auxiliary.surname}",
            icon = Icons.Filled.PersonPin,
            iconColor = Color(0xFF505050)
        )
    }
}

@Composable
fun VitalSignCard(vitalSign: VitalSignState, alertColor: Color, defaultInfoColor: Color) {
    CardContent(title = "Signes Vitals") {
        DetailItemWithIcon(
            label = "Tensió Arterial",
            info = "${vitalSign.systolicBloodPressure}mmHg/${vitalSign.diastolicBloodPressure}mmHg",
            icon = Icons.Filled.Favorite,
            iconColor = getBloodPressureColor(
                vitalSign.systolicBloodPressure, vitalSign.diastolicBloodPressure
            ),
            infoColor = getInfoColor(
                getBloodPressureColor(
                    vitalSign.systolicBloodPressure, vitalSign.diastolicBloodPressure
                ), alertColor, defaultInfoColor
            )
        )
        DetailItemWithIcon(
            label = "Feqüència Respiratòria",
            info = "${vitalSign.respiratoryRate} bpm",
            icon = Icons.Filled.MonitorHeart,
            iconColor = getRespiratoryRateColor(vitalSign.respiratoryRate),
            infoColor = getInfoColor(
                getRespiratoryRateColor(vitalSign.respiratoryRate), alertColor, defaultInfoColor
            )
        )
        DetailItemWithIcon(
            label = "Pols",
            info = "${vitalSign.pulse}",
            icon = Icons.Filled.Monitor,
            iconColor = getPulseColor(vitalSign.pulse),
            infoColor = getInfoColor(getPulseColor(vitalSign.pulse), alertColor, defaultInfoColor)
        )
        DetailItemWithIcon(
            label = "Temperatura",
            info = "${vitalSign.temperature} ºC",
            icon = Icons.Filled.DeviceThermostat,
            iconColor = getTemperatureColor(vitalSign.temperature),
            infoColor = getInfoColor(
                getTemperatureColor(vitalSign.temperature), alertColor, defaultInfoColor
            )
        )
        DetailItemWithIcon(
            label = "Saturació d'Oxigen",
            info = "${vitalSign.oxygenSaturation} %",
            icon = Icons.Filled.Air,
            iconColor = getOxygenSaturationColor(vitalSign.oxygenSaturation),
            infoColor = getInfoColor(
                getOxygenSaturationColor(vitalSign.oxygenSaturation), alertColor, defaultInfoColor
            )
        )
    }
}

@Composable
fun MobilizationCard(mobilization: MobilizationState) {
    CardContent(title = "Mobilització") {
        DetailItemWithIcon(
            label = "Sedació",
            info = "Nivel de tolerància: ${mobilization.sedestation}",
            icon = Icons.Filled.Vaccines
        )
        DetailItemWithIcon(
            label = "Deambulació",
            info = mobilization.walkingAssis.toWalkingAssisText(),
            icon = Icons.Filled.AssistWalker
        )
        if (mobilization.walkingAssis.toString().isNotBlank()) DetailItemWithIcon(
            label = "Tipus", info = mobilization.assisDesc, icon = Icons.Filled.Notes
        )
        DetailItemWithIcon(
            label = "Canvis posturals",
            info = mobilization.decubitus,
            icon = Icons.Filled.Rotate90DegreesCw
        )
    }
}

@Composable
fun DietCard(diet: DietState) {
    CardContent(title = "Dieta") {
        DetailItemWithIcon(
            label = "Data per a la dieta",
            info = diet.date!!.formatDate("dd/MM/yyyy"),
            icon = Icons.Filled.CalendarToday
        )
        DetailItemWithIcon(
            label = "Horari de la dieta", info = diet.takeData, icon = Icons.Filled.Dining
        )

        diet.dietTypeTexture?.let { texture ->
            DetailItemWithIcon(
                label = "Tipus de textura",
                info = texture.description,
                icon = Icons.Filled.FilterList
            )
        }

        diet.dietTypes?.forEach { dietType ->
            DetailItemWithIcon(
                label = "Tipus de dieta",
                info = dietType.description,
                icon = Icons.Filled.FilterList
            )
        }

        DetailItemWithIcon(
            label = "Necessita ajuda",
            info = diet.independent.toIndependentText(),
            icon = Icons.Filled.Help
        )
        DetailItemWithIcon(
            label = "Portador de protesis",
            info = diet.prosthesis.toProsthesisText(),
            icon = Icons.Filled.Person
        )
    }
}

@Composable
fun HygieneCard(hygiene: HygieneState) {
    CardContent(title = "Higiene") {
        DetailItemWithIcon(
            label = "Tipus", info = hygiene.description, icon = Icons.Filled.FilterList
        )
    }
}

@Composable
fun ObservationCard(observation: String) {
    CardContent(title = "Observacions") {
        DetailItemWithIcon(
            label = "Observacions del torn", info = observation, icon = Icons.Filled.Notes
        )
    }
}

@Composable
fun DrainCard(drain: DrainState) {
    CardContent(title = "Drenatge") {
        DetailItemWithIcon(
            label = "Quantitat", info = drain.output, icon = Icons.Filled.Output
        )
        DetailItemWithIcon(
            label = "Tipus", info = drain.type, icon = Icons.Filled.FilterList
        )
    }
}

@Composable
fun CardContent(title: String, content: @Composable () -> Unit) {
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
            Text(
                text = title, style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )
            content()
        }
    }
}

fun Date.formatDate(pattern: String): String {
    val outputFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
    return outputFormat.format(this)
}

fun getInfoColor(color: Color, alertColor: Color, defaultInfoColor: Color): Color {
    return if (color == Color.Red) alertColor else defaultInfoColor
}