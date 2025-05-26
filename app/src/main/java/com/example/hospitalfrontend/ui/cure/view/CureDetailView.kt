package com.example.hospitalfrontend.ui.cure.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCureDetail
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.diet.*
import com.example.hospitalfrontend.domain.model.medical.*
import com.example.hospitalfrontend.ui.diagnosis.view.*
import com.example.hospitalfrontend.utils.*
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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
                        text = stringResource(id = R.string.detail_care_title), style = TextStyle(
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
                register.vitalSign?.let { VitalSignCard(it, alertColor, defaultInfoColor) }

                // Only show cards if they have data
                register.diet?.let { diet ->
                    if (hasDietData(diet)) {
                        DietCard(diet)
                    }
                }

                register.drain?.let { drain ->
                    if (hasDrainData(drain)) {
                        DrainCard(drain)
                    }
                }

                register.hygieneType?.let { hygiene ->
                    if (hasHygieneData(hygiene)) {
                        HygieneCard(hygiene)
                    }
                }

                register.mobilization?.let { mobilization ->
                    if (hasMobilizationData(mobilization)) {
                        MobilizationCard(mobilization)
                    }
                }

                register.observation?.let { observation ->
                    if (observation.isNotEmpty()) {
                        ObservationCard(observation)
                    }
                }
            }
        }
    }
}

// Function to check if MobilizationState has data
private fun hasMobilizationData(mobilization: MobilizationState): Boolean {
    val hasSedestation = mobilization.sedestation.toString().isNotEmpty()
    val hasWalkingAssis = mobilization.walkingAssis.toString().isNotEmpty()
    val hasDecubitus = mobilization.decubitus.isNotEmpty()

    return hasSedestation || hasWalkingAssis || hasDecubitus
}

// Function to check if DietState has data
private fun hasDietData(diet: DietState): Boolean {
    val hasTakeData = diet.takeData?.isNotEmpty() == true
    val hasDietTypeTexture = diet.dietTypeTexture != null
    val hasDietTypes = diet.dietTypes.isNotEmpty() == true

    return hasTakeData || hasDietTypeTexture || hasDietTypes
}

// Function to check if DrainState has data
private fun hasDrainData(drain: DrainState): Boolean {
    val hasOutput = drain.output.isNotEmpty()
    val hasType = drain.type.isNotEmpty()

    return hasOutput || hasType
}

// Function to check if HygieneState has data
private fun hasHygieneData(hygiene: HygieneState): Boolean {
    return hygiene.description.isNotEmpty()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BasicInfoCard(data: RegisterState) {
    CardContent(title = "${data.patient.name} ${data.patient.surname}") {
        DetailItemWithIcon(
            labelRes = R.string.data,
            info = data.date?.formatDate("dd/MM/yyyy HH:mm") ?: "",
            icon = Icons.Filled.CalendarMonth,
            iconColor = Color(0xFF505050)
        )
        DetailItemWithIcon(
            labelRes = R.string.auxiliary_name,
            info = "${data.auxiliary.name} ${data.auxiliary.surname}",
            icon = Icons.Filled.PersonPin,
            iconColor = Color(0xFF505050)
        )
    }
}

@Composable
fun VitalSignCard(vitalSign: VitalSignState, alertColor: Color, defaultInfoColor: Color) {
    CardContent(title = stringResource(R.string.vital_signs)) {
        DetailItemWithIcon(
            labelRes = R.string.blood_pressure,
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
            labelRes = R.string.respiratory_rate,
            info = "${vitalSign.respiratoryRate} bpm",
            icon = Icons.Filled.MonitorHeart,
            iconColor = getRespiratoryRateColor(vitalSign.respiratoryRate),
            infoColor = getInfoColor(
                getRespiratoryRateColor(vitalSign.respiratoryRate), alertColor, defaultInfoColor
            )
        )
        DetailItemWithIcon(
            labelRes = R.string.pulse,
            info = "${vitalSign.pulse}",
            icon = Icons.Filled.Monitor,
            iconColor = getPulseColor(vitalSign.pulse),
            infoColor = getInfoColor(getPulseColor(vitalSign.pulse), alertColor, defaultInfoColor)
        )
        DetailItemWithIcon(
            labelRes = R.string.temperature,
            info = "${vitalSign.temperature} ºC",
            icon = Icons.Filled.DeviceThermostat,
            iconColor = getTemperatureColor(vitalSign.temperature),
            infoColor = getInfoColor(
                getTemperatureColor(vitalSign.temperature), alertColor, defaultInfoColor
            )
        )
        DetailItemWithIcon(
            labelRes = R.string.oxygen_saturation,
            info = "${vitalSign.oxygenSaturation} %",
            icon = Icons.Filled.Air,
            iconColor = getOxygenSaturationColor(vitalSign.oxygenSaturation),
            infoColor = getInfoColor(
                getOxygenSaturationColor(vitalSign.oxygenSaturation), alertColor, defaultInfoColor
            )
        )
        vitalSign.urineVolume?.let {
            DetailItemWithIcon(
                labelRes = R.string.urine_volume, info = "$it ml", icon = Icons.Filled.Air
            )
        }
        vitalSign.bowelMovements?.let {
            DetailItemWithIcon(
                labelRes = R.string.bowel_movements,
                info = "$it ml",
                icon = Icons.Filled.Air
            )

        }
        vitalSign.serumTherapy?.let {
            DetailItemWithIcon(
                labelRes = R.string.serum_therapy,
                info = "$it ml",
                icon = Icons.Filled.Air
            )
        }
    }
}

@Composable
fun MobilizationCard(mobilization: MobilizationState) {
    val context = LocalContext.current
    CardContent(title = stringResource(R.string.mobilization)) {
        DetailItemWithIcon(
            labelRes = R.string.sedestation,
            info = stringResource(R.string.tolerance_level, (mobilization.sedestation).toString()),
            icon = Icons.Filled.Vaccines
        )

        mobilization.walkingAssis?.let {
            DetailItemWithIcon(
                labelRes = R.string.wander,
                info = it.toWalkingAssisText(context),
                icon = Icons.Filled.AssistWalker
            )
            if (it == 1) {
                DetailItemWithIcon(
                    labelRes = R.string.type,
                    info = mobilization.assisDesc!!,
                    icon = Icons.Filled.Description
                )
            }
        }

        DetailItemWithIcon(
            labelRes = R.string.posture_changes,
            info = mobilization.decubitus,
            icon = Icons.Filled.Rotate90DegreesCw
        )
    }
}

@Composable
fun DietCard(diet: DietState) {
    val context = LocalContext.current
    CardContent(title = stringResource(R.string.diet)) {
        diet.date?.let {
            DetailItemWithIcon(
                labelRes = R.string.diet_data,
                info = it.formatDate("dd/MM/yyyy"),
                icon = Icons.Filled.CalendarToday
            )
        }
        diet.takeData?.let { date ->
            DetailItemWithIcon(
                labelRes = R.string.diet_time, info = date, icon = Icons.Filled.Dining
            )
        }

        diet.dietTypeTexture?.let { texture ->
            DetailItemWithIcon(
                labelRes = R.string.texture_type,
                info = texture.description,
                icon = Icons.Filled.FilterList
            )
        }

        diet.dietTypes.let { dietTypes ->
            if (dietTypes.isNotEmpty()) {
                DietTypesListItem(dietTypes)
            }
        }

        DetailItemWithIcon(
            labelRes = R.string.autonomy,
            info = diet.independent!!.toIndependentText(context),
            icon = Icons.AutoMirrored.Filled.Help
        )

        DetailItemWithIcon(
            labelRes = R.string.prosthesis,
            info = diet.prosthesis!!.toProsthesisText(context),
            icon = Icons.Filled.Person
        )
    }
}

@Composable
fun HygieneCard(hygiene: HygieneState) {
    CardContent(title = stringResource(R.string.hygiene)) {
        DetailItemWithIcon(
            labelRes = R.string.type,
            info = hygiene.description, icon = Icons.Filled.FilterList
        )
    }
}

@Composable
fun ObservationCard(observation: String) {
    CardContent(title = stringResource(R.string.observation_title)) {
        DetailItemWithIcon(
            labelRes = R.string.shift_remarks, info = observation, icon = Icons.Filled.Description
        )
    }
}

@Composable
fun DrainCard(drain: DrainState) {
    CardContent(title = stringResource(R.string.drain)) {
        DetailItemWithIcon(
            labelRes = R.string.quantity, info = drain.output, icon = Icons.Filled.Output
        )

        DetailItemWithIcon(
            labelRes = R.string.type, info = drain.type, icon = Icons.Filled.FilterList
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
    val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return outputFormat.format(this)
}

@RequiresApi(Build.VERSION_CODES.O)
fun OffsetDateTime.formatDate(pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return this.format(formatter)
}

fun getInfoColor(color: Color, alertColor: Color, defaultInfoColor: Color): Color {
    return if (color == Color.Red) alertColor else defaultInfoColor
}

@Composable
fun DietTypesListItem(dietTypes: Set<DietTypeState>) {
    val infoFontSize = 18.sp
    val labelFontSize = 20.sp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.FilterList,
            contentDescription = "Tipus de dieta",
            tint = Color(0xFF505050),
            modifier = Modifier.size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.diet_type),
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )

            dietTypes.forEach { dietType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = Color(0xFF7F8C8D),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Text(
                        text = dietType.description,
                        style = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontSize = infoFontSize,
                            color = Color(0xFF7F8C8D)
                        )
                    )
                }
            }
        }
    }
}