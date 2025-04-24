package com.example.hospitalfrontend.ui.nurses.view

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.*
import com.example.hospitalfrontend.network.PatientRemoteViewModel
import com.example.hospitalfrontend.network.RemoteApiMessageBoolean
import com.example.hospitalfrontend.ui.nurses.view.HospitalTheme.latoLightFont
import com.example.hospitalfrontend.ui.nurses.viewmodels.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel

object HospitalTheme {
    val Primary = Color(0xFF2C78E4)
    val Background = Color(0xFFA9C7C7)
    val Surface = Color.White
    val TextPrimary = Color(0xFF2C3E50)
    val Error = Color(0xFFE53935)
    val latoLightFont = FontFamily(Font(R.font.lato_light))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCureScreen(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    patientId: Int,
    auxiliaryViewModel: AuxiliaryViewModel,
    patientState: PatientState
) {
    val auxiliary = auxiliaryViewModel.getAuxiliaryState()

    LaunchedEffect(Unit) {
        patientRemoteViewModel.getPatientById(patientId, patientViewModel)
    }

    val patient by patientViewModel.patientState.collectAsState()

    var observation by remember { mutableStateOf("") }
    var hygieneType by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var drain by remember { mutableStateOf("") }
    var mobilization by remember { mutableStateOf("") }

    val remoteApiMessage = patientRemoteViewModel.remoteApiMessageBoolean.value
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }
    var systolic by rememberSaveable { mutableStateOf("") }
    var diastolic by rememberSaveable { mutableStateOf("") }
    var respiratoryRate by rememberSaveable { mutableStateOf("") }
    var pulse by rememberSaveable { mutableStateOf("") }
    var temperature by rememberSaveable { mutableStateOf("") }
    var urineVolume by rememberSaveable { mutableStateOf("") }
    var bowelMovements by rememberSaveable { mutableStateOf("") }
    var serumTherapy by rememberSaveable { mutableStateOf("") }
    var oxygenSaturation by rememberSaveable { mutableStateOf("") }

    var sedestation by rememberSaveable { mutableStateOf("") }
    var walkingAssis by rememberSaveable { mutableStateOf("") }
    var changes by rememberSaveable { mutableStateOf("") }
    var decubitus by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "NOVA CURA PER ${patientState.name}",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HospitalTheme.Surface)
            )
        },
        containerColor = HospitalTheme.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                VitalSignsCard(
                    systolic = systolic,
                    onSystolicChange = { systolic = it },
                    diastolic = diastolic,
                    onDiastolicChange = { diastolic = it },
                    respiratoryRate = respiratoryRate,
                    onRespiratoryRateChange = { respiratoryRate = it },
                    pulse = pulse,
                    onPulseChange = { pulse = it },
                    temperature = temperature,
                    onTemperatureChange = { temperature = it },
                    oxygenSaturation = oxygenSaturation,
                    onOxygenSaturationChange = { oxygenSaturation = it },
                    urineVolume = urineVolume,
                    onUrineVolumeChange = { urineVolume = it },
                    bowelMovements = bowelMovements,
                    onBowelMovementsChange = { bowelMovements = it },
                    serumTherapy = serumTherapy,
                    onSerumTherapyChange = { serumTherapy = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExpandableTextField(
                    label = "Drenatges (tipus y débit)",
                    icon = Icons.Filled.MedicalServices,
                    value = drain,
                    onValueChange = { drain = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HygieneSelection(
                    icon = Icons.Filled.Sanitizer,
                    value = hygieneType,
                    onValueChange = { hygieneType = it }
                )

                DietaSection()
                MobilitzacionsSection(
                    icon = Icons.Filled.AssistWalker,
                    value = mobilization,
                    onValueChange = { mobilization = it }
                )

                ExpandableTextField(
                    label = "Observacions",
                    icon = Icons.Filled.ContentPasteGo,
                    value = observation,
                    onValueChange = { observation = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                EnhancedSaveButton(
                    text = "Desar canvis",
                    isEnabled = systolic.isNotBlank() && diastolic.isNotBlank(),
                    fontFamily = latoLightFont
                ) {
                    val vitalSign = VitalSignState(
                        id = 0,
                        systolicBloodPressure = systolic.toDoubleOrNull() ?: 0.0,
                        diastolicBloodPressure = diastolic.toDoubleOrNull() ?: 0.0,
                        respiratoryRate = respiratoryRate.toDoubleOrNull() ?: 0.0,
                        pulse = pulse.toDoubleOrNull() ?: 0.0,
                        temperature = temperature.toDoubleOrNull() ?: 0.0,
                        oxygenSaturation = oxygenSaturation.toDoubleOrNull() ?: 0.0,
                        urineVolume = urineVolume.toDoubleOrNull() ?: 0.0,
                        bowelMovements = bowelMovements.toDoubleOrNull() ?: 0.0,
                        serumTherapy = serumTherapy.toDoubleOrNull() ?: 0.0,

                        )
                    val register = RegisterState(
                        id = 0,
                        date = null,
                        auxiliary = auxiliary!!,
                        patient = patient!!,
                        hygieneType = if (hygieneType.isNotBlank()) HygieneState(description = hygieneType) else null,
                        diet = if (diet.isNotBlank()) DietState(
                            id = 0,
                            date = null,
                            takeData = "Comida",
                            dietTypes = emptySet(),
                            dietTypeTexture = DietTextureTypeState(),
                            independent = 0,
                            prosthesis = 0
                        ) else null,
                        drain = if (drain.isNotBlank()) DrainState(
                            id = 0, output = "output",
                            type = drain
                        ) else null,
                        mobilization = if (mobilization.isNotBlank()) {
                            val parts = mobilization
                                .split(";")
                                .mapNotNull {
                                    val split = it.split("=")
                                    if (split.size == 2) split[0] to split[1] else null
                                }.toMap()

                            MobilizationState(
                                id = 0,
                                sedestation = parts["sedestation"]?.toIntOrNull() ?: 0,
                                walkingAssis = if (parts["walkingAssis"] == "Amb ajuda") 1 else 0,
                                assisDesc = parts["assisDesc"] ?: "",
                                changes = parts["changes"] ?: "",
                                decubitus = parts["decubitus"] ?: ""
                            )
                        } else null,

                                vitalSign = vitalSign,
                        observation = if (observation.isNotBlank()) observation else null
                    )
                    patientRemoteViewModel.createCure(register)
                }

                LaunchedEffect(remoteApiMessage) {
                    when (remoteApiMessage) {
                        is RemoteApiMessageBoolean.Success -> {
                            dialogMessage = "Data updated successfully."
                            showSuccessDialog = true
                            patientRemoteViewModel.clearApiMessage()
                        }

                        is RemoteApiMessageBoolean.Error -> {
                            Log.d("Error", "Error Save")
                            dialogMessage = "Failing to update data."
                            showErrorDialog = true
                            patientRemoteViewModel.clearApiMessage()
                        }

                        RemoteApiMessageBoolean.Loading -> Log.d("Loading Update", "Loading")
                    }
                }
            }
        }
    }
}

@Composable
fun VitalSignsCard(
    systolic: String,
    onSystolicChange: (String) -> Unit,
    diastolic: String,
    onDiastolicChange: (String) -> Unit,
    respiratoryRate: String,
    onRespiratoryRateChange: (String) -> Unit,
    pulse: String,
    onPulseChange: (String) -> Unit,
    temperature: String,
    onTemperatureChange: (String) -> Unit,
    oxygenSaturation: String,
    onOxygenSaturationChange: (String) -> Unit,
    urineVolume: String,
    onUrineVolumeChange: (String) -> Unit,
    bowelMovements: String,
    onBowelMovementsChange: (String) -> Unit,
    serumTherapy: String,
    onSerumTherapyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = HospitalTheme.Primary.copy(alpha = 0.2f)
            ),
        colors = CardDefaults.cardColors(containerColor = HospitalTheme.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            SectionTitle("Constants Vitals")
            Spacer(modifier = Modifier.height(16.dp))

            VitalSignsGroup(title = "Tensió Arterial", icon = Icons.Filled.Favorite) {
                VitalTextField(
                    label = "Sistólica",
                    placeholder = "mmHg",
                    value = systolic,
                    onValueChange = onSystolicChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                VitalTextField(
                    label = "Diastólica",
                    placeholder = "mmHg",
                    value = diastolic,
                    onValueChange = onDiastolicChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            VitalSignTextField(
                label = "Freqüència Respiratòria",
                icon = Icons.Filled.MonitorHeart,
                placeholder = "x'",
                value = respiratoryRate,
                onValueChange = onRespiratoryRateChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            VitalSignTextField(
                label = "Pols",
                icon = Icons.Filled.Monitor,
                placeholder = "x'",
                value = pulse,
                onValueChange = onPulseChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            VitalSignTextField(
                label = "Temperatura",
                icon = Icons.Filled.DeviceThermostat,
                placeholder = "°C",
                value = temperature,
                onValueChange = onTemperatureChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            VitalSignTextField(
                label = "Saturació d'Oxigen",
                icon = Icons.Filled.Air,
                placeholder = "%",
                value = oxygenSaturation,
                onValueChange = onOxygenSaturationChange
            )
            VitalSignTextField(
                label = "Volum d'orina",
                icon = Icons.Filled.Air,
                placeholder = "mL",
                value = urineVolume,
                onValueChange = onUrineVolumeChange
            )
            VitalSignTextField(
                label = "Moviments intestinals",
                icon = Icons.Filled.Air,
                placeholder = "mL",
                value = bowelMovements,
                onValueChange = onBowelMovementsChange
            )
            VitalSignTextField(
                label = "Terapia amb sèrum",
                icon = Icons.Filled.Air,
                placeholder = "mL",
                value = serumTherapy,
                onValueChange = onSerumTherapyChange
            )
        }
    }
}

@Composable
fun EnhancedSaveButton(
    text: String,
    isEnabled: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(56.dp)
            .padding(vertical = 8.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(151, 199, 150),
            disabledContainerColor = Color.LightGray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                color = Color.White
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    )
}

@Composable
fun VitalSignsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )
        }
        content()
    }
}

@Composable
fun VitalTextField(
    label: String,
    placeholder: String = "",
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        textStyle = TextStyle(
            fontFamily = FontFamily.Default,
            fontSize = 16.sp,
            color = HospitalTheme.TextPrimary
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            cursorColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun VitalSignTextField(
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 16.sp,
                color = HospitalTheme.TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
fun ExpandableTextField(
    label: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color.Black
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontSize = 16.sp,
                    color = HospitalTheme.TextPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                minLines = 3
            )
        }
    }
}

@Composable
fun HygieneSelection(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    val options = listOf("Allitat", "Parcial al llit", "Dutxa amb ajuda", "Autònom")
    var selectedOption by remember { mutableStateOf(options[0]) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp)
        ) {
            Text(
                text = "Higiene",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expandir o contraer"
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = option
                                isExpanded = false
                                onValueChange(option)
                            }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                selectedOption = option
                                isExpanded = false
                                onValueChange(option)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MobilitzacionsSection(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var sedestation by remember { mutableStateOf("") }
    var walkingAssis by remember { mutableStateOf("Sense ajuda") }
    var assisDesc by remember { mutableStateOf("") }
    var changes by remember { mutableStateOf("") }
    var decubitus by remember { mutableStateOf("") }

    val tipusAjudaOptions = listOf("Bastó", "Caminador", "Ajuda Física")
    val decubitoOptions = listOf("Supí", "Lateral E", "Lateral D")

    // 👇 Esta función construye un resumen del estado actual
    fun updateMobilizationValue() {
        val newValue = listOf(
            "sedestation=$sedestation",
            "walkingAssis=$walkingAssis",
            "assisDesc=$assisDesc",
            "changes=$changes",
            "decubitus=$decubitus"
        ).joinToString(";")
        onValueChange(newValue)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp)
        ) {
            Text(
                text = "Mobilitzacions",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "Expand Icon"
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sedestació",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            OutlinedTextField(
                value = sedestation,
                onValueChange = { sedestation = it },
                label = { Text("Tolerància") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Deambulació",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            walkingAssis = "Sense ajuda"; assisDesc = ""; updateMobilizationValue()
                        }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (walkingAssis == "Sense ajuda"),
                        onClick = {
                            walkingAssis = "Sense ajuda"
                            assisDesc = ""
                            updateMobilizationValue()
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sense ajuda", fontSize = 16.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            walkingAssis = "Amb ajuda"
                            updateMobilizationValue()
                        }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (walkingAssis == "Amb ajuda"),
                        onClick = { walkingAssis = "Amb ajuda"
                            updateMobilizationValue()}
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Amb ajuda", fontSize = 16.sp)
                }
            }

            if (walkingAssis == "Amb ajuda") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tipus d'ajuda",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )
                tipusAjudaOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                assisDesc = option
                                updateMobilizationValue()
                            }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = (assisDesc == option),
                            onClick = { assisDesc = option
                                updateMobilizationValue()}
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Canvis posturals",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            OutlinedTextField(
                value = changes,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) {
                        changes = it
                        updateMobilizationValue()
                    }
                },
                label = { Text("Quants canvis han hagut?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Decúbits",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            decubitoOptions.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            decubitus = option
                            updateMobilizationValue()
                        }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (decubitus == option),
                        onClick = {
                            decubitus = option
                            updateMobilizationValue()
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(option, fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietaSection() {
    var isExpanded by remember { mutableStateOf(false) }

    var selectedTexturaDieta by remember { mutableStateOf("") }
    val texturaDietaOptions = listOf(
        "Absoluta",
        "Hídrica (quantitat diària)",
        "Líquida",
        "Túrmix",
        "Semitova",
        "Tova",
        "Fàcil masticació",
        "Basal vegetariana",
        "Basal vegana",
        "Basal halal",
        "Basal mediterrània"
    )
    var expandedTexturaDieta by remember { mutableStateOf(false) }

    var expandedTipoDieta by remember { mutableStateOf(false) }
    val selectedTipoDieta = remember { mutableStateListOf<String>() }
    val tipoDietaOptions = listOf(
        "Diabètica",
        "Hipolipídica",
        "Hipocalòrica",
        "Hipercalòrica",
        "Hipoproteica",
        "Hiperproteica",
        "Astringent",
        "Baixa en residus",
        "Celíaca",
        "Rica en fibra",
        "Sense lactosa",
        "Sense fruits secs",
        "Sense ou",
        "Sense porc"
    )

    var selectedAutonomia by remember { mutableStateOf("Autónomo") }
    val autonomiaOptions = listOf("Autónomo", "Ayuda")

    var selectedPortadorProtesis by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp)
        ) {
            Text(
                text = "Dieta",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "Expand Icon"
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedTexturaDieta,
                onExpandedChange = { expandedTexturaDieta = !expandedTexturaDieta }
            ) {
                OutlinedTextField(
                    value = selectedTexturaDieta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Textura de la dieta") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedTexturaDieta
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedTexturaDieta,
                    onDismissRequest = { expandedTexturaDieta = false }
                ) {
                    texturaDietaOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            selectedTexturaDieta = option
                            expandedTexturaDieta = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box {
                OutlinedTextField(
                    value = if (selectedTipoDieta.isEmpty()) "" else selectedTipoDieta.joinToString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipus de dieta") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.clickable { expandedTipoDieta = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedTipoDieta = true }
                )

                DropdownMenu(
                    expanded = expandedTipoDieta,
                    onDismissRequest = { expandedTipoDieta = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tipoDietaOptions.forEach { option ->
                        DropdownMenuItem(text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedTipoDieta.contains(option),
                                    onCheckedChange = {
                                        if (it) selectedTipoDieta.add(option)
                                        else selectedTipoDieta.remove(option)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(option)
                            }
                        }, onClick = {
                            if (selectedTipoDieta.contains(option)) {
                                selectedTipoDieta.remove(option)
                            } else {
                                selectedTipoDieta.add(option)
                            }
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                autonomiaOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = selectedAutonomia == option,
                            onClick = { selectedAutonomia = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Portador de prótesis")
            Row(verticalAlignment = Alignment.CenterVertically) {
                val options = listOf("Sí", "No")
                options.forEachIndexed { index, option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = selectedPortadorProtesis == index,
                            onClick = { selectedPortadorProtesis = index }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        }
    }
}
