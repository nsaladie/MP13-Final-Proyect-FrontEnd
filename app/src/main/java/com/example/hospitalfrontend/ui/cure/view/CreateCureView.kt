package com.example.hospitalfrontend.ui.cure.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.*
import com.example.hospitalfrontend.data.remote.viewmodel.*
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.diet.*
import com.example.hospitalfrontend.domain.model.medical.*
import com.example.hospitalfrontend.domain.model.patient.*
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.medication.view.StatusDialog
import com.example.hospitalfrontend.utils.*
import java.text.SimpleDateFormat
import java.util.*

object HospitalTheme {
    val Primary = Color(0xFF505050)
    val Background = Color(0xFFA9C7C7)
    val BackgroundMuted = Color(0xFF7F8C8D)
    val Surface = Color.White
    val TextPrimary = Color(0xFF2C3E50)
    val TextSecondary = Color(0xFF546E7A)
    val Error = Color(0xFFE53935)
    val Success = Color(0xFF43A047)
    val IconColor = Color(0xFF3498DB)
    val ColumColor = Color(0xFFA9C7C7)
    val SaveColor = Color(151, 199, 150)
    val latoBoldFont = FontFamily(Font(R.font.lato_regular))
}

@Composable
fun CreateCureScreen(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientId: Int,
    auxiliaryViewModel: AuxiliaryViewModel,
    dietRemoteViewModel: DietRemoteViewModel = viewModel()
) {
    val auxiliary = auxiliaryViewModel.getAuxiliaryState()
    val patient = PatientState(historialNumber = patientId)

    // Form state management
    var observation by remember { mutableStateOf("") }
    var dietState by remember { mutableStateOf(DietState()) }
    var vitalSignState by remember { mutableStateOf(VitalSignState()) }
    var mobilizationState by remember { mutableStateOf(MobilizationState()) }
    var hygieneState by remember { mutableStateOf(HygieneState()) }
    var drainState by remember { mutableStateOf(DrainState()) }

    // Dialog state
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    var showOutOfRangeDialog by rememberSaveable { mutableStateOf(false) }
    var outOfRangeMessages by rememberSaveable { mutableStateOf(listOf<String>()) }

    val remoteApiMessage = patientRemoteViewModel.remoteApiMessageBoolean.value

    val isFormValid = remember(
        vitalSignState, dietState, hygieneState, mobilizationState, drainState
    ) {
        val vitalSignsValid = with(vitalSignState) {
            systolicBloodPressure > 0 && diastolicBloodPressure > 0 &&
                    respiratoryRate > 0 && pulse > 0 &&
                    temperature > 0 && oxygenSaturation > 0
        }

        // Check if any diet field is filled
        val isDietStarted = with(dietState) {
            date != null || takeData != null || dietTypes.isNotEmpty() ||
                    dietTypeTexture != null || independent != null || prosthesis != null
        }

        // If diet started, check all mandatory fields
        val dietValid = if (isDietStarted) {
            with(dietState) {
                date != null && takeData != null && dietTypes.isNotEmpty() &&
                        dietTypeTexture != null && independent != null && prosthesis != null
            }
        } else true

        // Check if any hygiene field is filled
        val isHygieneStarted = hygieneState.description.isNotBlank()
        val hygieneValid = if (isHygieneStarted) {
            hygieneState.description.isNotBlank()
        } else true

        // Check if any drain field is filled
        val isDrainStarted = with(drainState) {
            output.isNotBlank() || type.isNotBlank()
        }
        val drainValid = if (isDrainStarted) {
            with(drainState) {
                output.isNotBlank() && type.isNotBlank()
            }
        } else true

        // Check if any mobilization field is filled
        val isMobilizationStarted = with(mobilizationState) {
            sedestation != null || walkingAssis != null ||
                    assisDesc != null || changes.isNotBlank() || decubitus.isNotBlank()
        }
        val mobilizationValid = if (isMobilizationStarted) {
            with(mobilizationState) {
                sedestation != null && walkingAssis != null &&
                        (walkingAssis != 1 || assisDesc != null) &&
                        changes.isNotBlank() && decubitus.isNotBlank()
            }
        } else true

        vitalSignsValid && dietValid && hygieneValid && drainValid && mobilizationValid
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            HospitalTopAppBar(
                title = stringResource(id = R.string.create_care_title),
                onCloseClick = { navController.popBackStack() })
        }, bottomBar = {
            HospitalBottomBar(
                text = stringResource(id = R.string.button_save_changes),
                isEnabled = isFormValid,
                fontFamily = HospitalTheme.latoBoldFont
            ) {
                val outOfRange = getVitalSignOutOfRangeMessages(context, vitalSignState)

                if (outOfRange.isNotEmpty()) {
                    outOfRangeMessages = outOfRange
                    showOutOfRangeDialog = true
                } else {
                    val register = RegisterState(
                        id = 0,
                        date = null,
                        auxiliary = auxiliary!!,
                        patient = patient,
                        hygieneType = hygieneState.takeIf { it.description.isNotBlank() },
                        diet = dietState.takeIf {
                            it.date != null || it.takeData != null || it.dietTypes.isNotEmpty() ||
                                    it.dietTypeTexture != null || it.independent != null || it.prosthesis != null
                        },
                        drain = drainState.takeIf { it.output.isNotBlank() || it.type.isNotBlank() },
                        mobilization = mobilizationState.takeIf {
                            it.sedestation != null || it.walkingAssis != null ||
                                    it.assisDesc != null || it.changes.isNotBlank() || it.decubitus.isNotBlank()
                        },
                        vitalSign = vitalSignState,
                        observation = observation.takeIf { it.isNotBlank() }
                    )
                    patientRemoteViewModel.createCure(register)
                }
            }
        }, containerColor = HospitalTheme.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                VitalSignsCard(onVitalSignStateChange = { vitalSignState = it })
                Spacer(modifier = Modifier.height(20.dp))

                FormSection(
                    title = stringResource(id = R.string.diet),
                    icon = Icons.Outlined.Restaurant
                ) {
                    DietSection(
                        dietState = dietState,
                        onDietStateChange = { dietState = it },
                        dietRemoteViewModel = dietRemoteViewModel
                    )
                }

                FormSection(
                    title = stringResource(id = R.string.drain),
                    icon = Icons.Outlined.MedicalServices
                ) {
                    DrainSection(onDrainStateChange = { drainState = it })
                }

                FormSection(
                    title = stringResource(id = R.string.hygiene),
                    icon = Icons.Outlined.CleanHands
                ) {
                    HygieneSelection(onHygieneStateChange = { hygieneState = it })
                }

                FormSection(
                    title = stringResource(id = R.string.mobilization),
                    icon = Icons.Outlined.AccessibilityNew
                ) {
                    MobilizationSection(onMobilizationStateChange = { mobilizationState = it })
                }

                FormSection(
                    title = stringResource(id = R.string.observation_title),
                    icon = Icons.Outlined.ContentPasteGo
                ) {
                    ObservationsField(
                        value = observation, onValueChange = { observation = it })
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        val successMessage = stringResource(R.string.cure_created_successfully)
        val errorMessage = stringResource(R.string.cure_creation_error)

        LaunchedEffect(remoteApiMessage) {
            when (remoteApiMessage) {
                is RemoteApiMessageBoolean.Success -> {
                    dialogMessage = successMessage
                    showSuccessDialog = true
                    patientRemoteViewModel.clearApiMessage()
                }

                is RemoteApiMessageBoolean.Error -> {
                    dialogMessage =
                        errorMessage
                    showErrorDialog = true
                    patientRemoteViewModel.clearApiMessage()
                }

                RemoteApiMessageBoolean.Loading -> {

                }
            }
        }

        // Dialog management
        if (showSuccessDialog) {
            StatusDialog(
                title = stringResource(R.string.success),
                message = dialogMessage,
                icon = Icons.Filled.CheckCircle,
                iconTint = Color.Green,
                onDismiss = {
                    showErrorDialog = false
                    navController.popBackStack()
                }
            )
        }

        if (showErrorDialog) {
            StatusDialog(
                title = stringResource(R.string.error_title),
                message = dialogMessage,
                icon = Icons.Filled.Error,
                iconTint = Color.Red,
                onDismiss = {
                    showErrorDialog = false
                }
            )
        }
        if (showOutOfRangeDialog) {
            OutOfRangeDialog(
                title = stringResource(id = R.string.dialog_title_range),
                message = outOfRangeMessages.joinToString("\n"),
                icon = Icons.Outlined.Warning,
                iconTint = Color(0xFFFF9800),
                onConfirm = {
                    showOutOfRangeDialog = false
                    val register = RegisterState(
                        id = 0,
                        date = null,
                        auxiliary = auxiliary!!,
                        patient = patient,
                        hygieneType = hygieneState.takeIf { it.description.isNotBlank() },
                        diet = dietState.takeIf {
                            it.date != null || it.takeData != null || it.dietTypes.isNotEmpty() ||
                                    it.dietTypeTexture != null || it.independent != null || it.prosthesis != null
                        },
                        drain = drainState.takeIf { it.output.isNotBlank() || it.type.isNotBlank() },
                        mobilization = mobilizationState.takeIf {
                            it.sedestation != null || it.walkingAssis != null ||
                                    it.assisDesc != null || it.changes.isNotBlank() || it.decubitus.isNotBlank()
                        },
                        vitalSign = vitalSignState,
                        observation = observation.takeIf { it.isNotBlank() }
                    )
                    patientRemoteViewModel.createCure(register)
                },
                onCancel = {
                    showOutOfRangeDialog = false
                }
            )
        }
    }
}

@Composable
fun OutOfRangeDialog(
    title: String,
    message: String,
    icon: ImageVector,
    iconTint: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .width(280.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HospitalTheme.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 18.sp,
                        color = HospitalTheme.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HospitalTheme.Primary
                        ),
                        border = BorderStroke(1.dp, HospitalTheme.Primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_cancel),
                            style = TextStyle(
                                fontFamily = LatoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Continue Button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HospitalTheme.Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_continue),
                            style = TextStyle(
                                fontFamily = LatoFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

fun getVitalSignOutOfRangeMessages(context: Context, vitals: VitalSignState): List<String> {
    val messages = mutableListOf<String>()

    if (vitals.systolicBloodPressure < SYSTOLIC_LOW || vitals.systolicBloodPressure > SYSTOLIC_HIGH) {
        messages.add(
            context.getString(R.string.systolic_pressure_range, vitals.systolicBloodPressure)
        )
    }
    if (vitals.diastolicBloodPressure < DIASTOLIC_LOW || vitals.diastolicBloodPressure > DIASTOLIC_HIGH) {
        messages.add(
            context.getString(R.string.diastolic_pressure_range, vitals.diastolicBloodPressure)
        )
    }
    if (vitals.respiratoryRate < RESPIRATORY_RATE_LOW || vitals.respiratoryRate > RESPIRATORY_RATE_HIGH) {
        messages.add(
            context.getString(R.string.respiratory_rate_range, vitals.respiratoryRate)
        )
    }
    if (vitals.pulse < PULSE_LOW || vitals.pulse > PULSE_HIGH) {
        messages.add(
            context.getString(R.string.pulse_range, vitals.pulse)
        )
    }
    if (vitals.temperature < TEMPERATURE_LOW || vitals.temperature > TEMPERATURE_HIGH) {
        messages.add(
            context.getString(R.string.temperature_range, vitals.temperature)
        )
    }
    if (vitals.oxygenSaturation < OXYGEN_SATURATION_LOW) {
        messages.add(
            context.getString(R.string.oxygen_saturation_range, vitals.oxygenSaturation)
        )
    }

    return messages
}


@Composable
fun FormSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    ExpandableCard(title = title, icon = icon) { content() }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun ObservationsField(value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .heightIn(min = 120.dp, max = 300.dp),
            textStyle = TextStyle(
                fontFamily = LatoFontFamily, fontSize = 18.sp, color = HospitalTheme.TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HospitalTheme.Primary,
                unfocusedBorderColor = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
                focusedLabelColor = HospitalTheme.Primary,
                cursorColor = HospitalTheme.Primary
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = 3,
            maxLines = 10
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalTopAppBar(title: String, onCloseClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title, style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoFontFamily,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
            )
        }, navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Black)
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = HospitalTheme.Background
        ), modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
fun HospitalBottomBar(
    text: String, isEnabled: Boolean, fontFamily: FontFamily, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HospitalTheme.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        EnhancedSaveButton(
            text = text, isEnabled = isEnabled, fontFamily = fontFamily, onClick = onClick
        )
    }
}

@Composable
fun ExpandableCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = HospitalTheme.Primary.copy(alpha = 0.1f)
            ),
        colors = CardDefaults.cardColors(containerColor = HospitalTheme.Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HospitalTheme.IconColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title, style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = HospitalTheme.TextPrimary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isExpanded) HospitalTheme.Primary.copy(0.1f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = HospitalTheme.Primary
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(bottom = 16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun VitalSignsCard(onVitalSignStateChange: (VitalSignState) -> Unit) {
    // Form state
    var systolic by rememberSaveable { mutableStateOf("") }
    var diastolic by rememberSaveable { mutableStateOf("") }
    var respiratoryRate by rememberSaveable { mutableStateOf("") }
    var pulse by rememberSaveable { mutableStateOf("") }
    var temperature by rememberSaveable { mutableStateOf("") }
    var oxygenSaturation by rememberSaveable { mutableStateOf("") }
    var urineVolume by rememberSaveable { mutableStateOf("") }
    var bowelMovements by rememberSaveable { mutableStateOf("") }
    var serumTherapy by rememberSaveable { mutableStateOf("") }

    // Reusable decimal validation function
    val validateDecimal: (String, String) -> String = { newValue, currentValue ->
        val processed = newValue.replace(',', '.')
        if (processed.isEmpty() || processed.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            processed
        } else {
            // Return previous value if invalid
            currentValue
        }
    }

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
            VitalSignsHeader(
                title = stringResource(id = R.string.vital_signs),
                icon = Icons.Outlined.MonitorHeart
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HospitalTheme.ColumColor.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.blood_pressure_required), style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = HospitalTheme.TextPrimary
                    ), modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VitalSignNumberField(
                        value = systolic,
                        onValueChange = { systolic = validateDecimal(it, systolic) },
                        label = stringResource(id = R.string.systolic),
                        placeholder = "mmHg",
                        modifier = Modifier.weight(1f)
                    )

                    VitalSignNumberField(
                        value = diastolic,
                        onValueChange = { diastolic = validateDecimal(it, diastolic) },
                        label = stringResource(id = R.string.diastolic),
                        placeholder = "mmHg",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            VitalSignMeasurementField(
                label = stringResource(id = R.string.respiratory_rate_required),
                value = respiratoryRate,
                onValueChange = { respiratoryRate = validateDecimal(it, respiratoryRate) },
                unit = "x'"
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.pulse_required),
                value = pulse,
                onValueChange = { pulse = validateDecimal(it, pulse) },
                unit = "x'"
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.temperature_required),
                value = temperature,
                onValueChange = { temperature = validateDecimal(it, temperature) },
                unit = "°C"
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.oxygen_saturation_required),
                value = oxygenSaturation,
                onValueChange = { oxygenSaturation = validateDecimal(it, oxygenSaturation) },
                unit = "%"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = HospitalTheme.BackgroundMuted
            )

            Text(
                text = stringResource(id = R.string.additional_measures), style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = NunitoFontFamily,
                    color = HospitalTheme.TextPrimary
                ), modifier = Modifier.padding(bottom = 16.dp)
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.urine_volume),
                value = urineVolume,
                onValueChange = { urineVolume = validateDecimal(it, urineVolume) },
                unit = "mL"
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.bowel_movements),
                value = bowelMovements,
                onValueChange = { bowelMovements = validateDecimal(it, bowelMovements) },
                unit = "mL"
            )

            VitalSignMeasurementField(
                label = stringResource(id = R.string.serum_therapy),
                value = serumTherapy,
                onValueChange = { serumTherapy = validateDecimal(it, serumTherapy) },
                unit = "mL"
            )
        }
    }

    // Update the parent with the new VitalSignState whenever any value changes
    LaunchedEffect(
        systolic,
        diastolic,
        respiratoryRate,
        pulse,
        temperature,
        oxygenSaturation,
        urineVolume,
        bowelMovements,
        serumTherapy
    ) {
        val vitalSignState = VitalSignState(
            systolicBloodPressure = systolic.toDoubleOrNull() ?: 0.0,
            diastolicBloodPressure = diastolic.toDoubleOrNull() ?: 0.0,
            respiratoryRate = respiratoryRate.toDoubleOrNull() ?: 0.0,
            pulse = pulse.toDoubleOrNull() ?: 0.0,
            temperature = temperature.toDoubleOrNull() ?: 0.0,
            oxygenSaturation = oxygenSaturation.toDoubleOrNull() ?: 0.0,
            urineVolume = urineVolume.toDoubleOrNull(),
            bowelMovements = bowelMovements.toDoubleOrNull(),
            serumTherapy = serumTherapy.toDoubleOrNull()
        )
        onVitalSignStateChange(vitalSignState)
    }
}

@Composable
fun VitalSignNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        textStyle = TextStyle(
            fontFamily = NunitoFontFamily, fontSize = 18.sp, color = HospitalTheme.TextPrimary
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HospitalTheme.Primary,
            unfocusedBorderColor = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
            focusedLabelColor = HospitalTheme.Primary,
            cursorColor = HospitalTheme.Primary
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@Composable
fun VitalSignMeasurementField(
    label: String, value: String, onValueChange: (String) -> Unit, unit: String
) {
    HospitalTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = unit,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        trailingIcon = {
            Text(
                text = unit, style = TextStyle(
                    fontSize = 16.sp, color = HospitalTheme.TextSecondary
                ), modifier = Modifier.padding(end = 12.dp)
            )
        })
}

@Composable
fun VitalSignsHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(HospitalTheme.IconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title, style = TextStyle(
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = HospitalTheme.TextPrimary
            )
        )
    }
}

@Composable
fun DrainSection(onDrainStateChange: (DrainState) -> Unit) {
    var drainType by rememberSaveable { mutableStateOf("") }
    var drainOutput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        HospitalTextField(
            label = stringResource(id = R.string.drain_type),
            value = drainType,
            onValueChange = { drainType = it },
            placeholder = stringResource(id = R.string.drain_type_ex)
        )

        Spacer(modifier = Modifier.height(16.dp))

        HospitalTextField(
            label = stringResource(id = R.string.debit_quantity),
            value = drainOutput,
            onValueChange = {
                drainOutput = it
            },
            placeholder = "mL",
            trailingIcon = {
                Text(
                    text = "mL", style = TextStyle(
                        fontSize = 16.sp, color = HospitalTheme.TextSecondary
                    ), modifier = Modifier.padding(end = 12.dp)
                )
            })

        LaunchedEffect(drainType, drainOutput) {
            val drainState = DrainState(
                type = drainType, output = drainOutput
            )
            onDrainStateChange(drainState)
        }
    }
}

// Base text field component
@Composable
fun HospitalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label, style = TextStyle(
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Medium,
                color = HospitalTheme.TextSecondary
            ), modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    placeholder, color = HospitalTheme.TextSecondary.copy(alpha = 0.6f)
                )
            },
            textStyle = TextStyle(
                fontFamily = LatoFontFamily, fontSize = 18.sp, color = HospitalTheme.TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HospitalTheme.Primary,
                unfocusedBorderColor = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
                focusedLabelColor = HospitalTheme.Primary,
                cursorColor = HospitalTheme.Primary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon
        )
    }
}

@Composable
fun EnhancedRadioGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    isHorizontal: Boolean = options.size <= 2,
    haveBorder: Boolean = true,
    hasBackground: Boolean = true,
    additionalContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (hasBackground) HospitalTheme.ColumColor.copy(alpha = 0.3f) else Color.Transparent
            )
            .padding(16.dp)
    ) {
        Text(
            text = title, style = TextStyle(
                fontSize = 18.sp,
                fontFamily = LatoFontFamily,
                fontWeight = FontWeight.Medium,
                color = HospitalTheme.TextPrimary
            ), modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isHorizontal) {
            // Horizontal layout for 2 or fewer options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { option ->
                    EnhancedRadioButton(
                        option = option,
                        selectedOption = selectedOption,
                        onOptionSelected = onOptionSelected,
                        haveBorder = haveBorder
                    )
                }
            }
        } else {
            // Vertical layout for more than 2 options
            Column(modifier = Modifier.fillMaxWidth()) {
                options.forEach { option ->
                    EnhancedRadioButton(
                        option = option,
                        selectedOption = selectedOption,
                        onOptionSelected = onOptionSelected,
                        haveBorder = haveBorder
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        additionalContent()
    }
}

@Composable
fun EnhancedRadioButton(
    option: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    haveBorder: Boolean = true
) {
    val isSelected = option == selectedOption

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) HospitalTheme.Primary.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .then(
                if (haveBorder) {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isSelected) HospitalTheme.Primary
                        else HospitalTheme.TextSecondary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .clickable { onOptionSelected(option) }
            .padding(vertical = 10.dp, horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = { onOptionSelected(option) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = HospitalTheme.Primary,
                    unselectedColor = HospitalTheme.TextSecondary
                ),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = option, style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = LatoFontFamily,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) HospitalTheme.Primary else HospitalTheme.TextPrimary
                )
            )
        }
    }
}

@Composable
fun HygieneSelection(onHygieneStateChange: (HygieneState) -> Unit) {
    val options = listOf(
        stringResource(id = R.string.hygiene_option_bedridden),
        stringResource(id = R.string.hygiene_option_partial_bed),
        stringResource(id = R.string.hygiene_option_helped_shower),
        stringResource(id = R.string.hygiene_option_autonomous)
    )
    var selectedOption by rememberSaveable { mutableStateOf("") }

    EnhancedRadioGroup(
        title = stringResource(id = R.string.type_hygiene_required),
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = {
            selectedOption = it
            onHygieneStateChange(HygieneState(description = it))
        },
        haveBorder = false
    )
}

@Composable
fun MobilizationSection(onMobilizationStateChange: (MobilizationState) -> Unit) {
    var sedestation by rememberSaveable { mutableStateOf("") }
    var walkingAssis by rememberSaveable { mutableStateOf("") }
    var assisDesc by rememberSaveable { mutableStateOf("") }
    var changes by rememberSaveable { mutableStateOf("") }
    var decubitus by rememberSaveable { mutableStateOf("") }
    val withHelp = stringResource(R.string.mobilization_walking_with_help)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        HospitalTextField(
            label = stringResource(R.string.mobilization_sedestation_label),
            value = sedestation,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]+$"))) {
                    sedestation = newValue
                }
            },
            placeholder = stringResource(R.string.mobilization_sedestation_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = stringResource(R.string.mobilization_walking_title),
            options = listOf(
                stringResource(R.string.mobilization_walking_no_help),
                stringResource(R.string.mobilization_walking_with_help)
            ),
            selectedOption = walkingAssis,
            onOptionSelected = {
                walkingAssis = it
                assisDesc =
                    if (it == withHelp) assisDesc else ""
            }) {
            // Only show assistance options when "Amb ajuda" is selected
            if (walkingAssis == stringResource(R.string.mobilization_walking_with_help)) {
                Spacer(modifier = Modifier.height(12.dp))
                EnhancedRadioGroup(
                    title = stringResource(R.string.mobilization_help_type_title),
                    options = listOf(
                        stringResource(R.string.mobilization_help_type_cane),
                        stringResource(R.string.mobilization_help_type_walker),
                        stringResource(R.string.mobilization_help_type_physical)
                    ),
                    selectedOption = assisDesc,
                    onOptionSelected = { assisDesc = it },
                    haveBorder = false,
                    hasBackground = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HospitalTextField(
            label = stringResource(R.string.mobilization_changes_label),
            value = changes,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    changes = newValue
                }
            },
            placeholder = stringResource(R.string.mobilization_changes_placeholder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = stringResource(R.string.mobilization_decubitus_title),
            options = listOf(
                stringResource(R.string.mobilization_decubitus_supine),
                stringResource(R.string.mobilization_decubitus_lateral_left),
                stringResource(R.string.mobilization_decubitus_lateral_right)
            ),
            selectedOption = decubitus,
            onOptionSelected = { decubitus = it })
    }
    // Update state when any field changes
    LaunchedEffect(sedestation, walkingAssis, assisDesc, changes, decubitus) {
        val mobilizationState = MobilizationState(
            sedestation = sedestation.toIntOrNull(),
            walkingAssis = if (walkingAssis.isNotEmpty()) {
                if (walkingAssis == withHelp) 1 else 0
            } else {
                null
            },
            assisDesc = if (assisDesc.isNotEmpty()) assisDesc else null,
            changes = changes,
            decubitus = decubitus
        )
        onMobilizationStateChange(mobilizationState)
    }
}

// Simple scroll position indicator
@Composable
fun ScrollPositionIndicator(
    scrollState: ScrollState, modifier: Modifier = Modifier
) {
    if (scrollState.maxValue > 0) {
        val scrollPercentage = scrollState.value.toFloat() / scrollState.maxValue.toFloat()

        Box(
            modifier = modifier
                .width(4.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(HospitalTheme.TextSecondary.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(16.dp)
                    .offset(x = 0.dp, y = (64 * scrollPercentage).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HospitalTheme.Primary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietSection(
    dietState: DietState,
    onDietStateChange: (DietState) -> Unit,
    dietRemoteViewModel: DietRemoteViewModel = viewModel()
) {
    val isExpanded by remember { mutableStateOf(false) }

    // Diet texture state
    var selectedTextureDiet by remember {
        mutableStateOf(
            if (isExpanded) dietState.dietTypeTexture?.description ?: "" else ""
        )
    }
    var selectedTextureDietId by remember {
        mutableIntStateOf(
            if (isExpanded) dietState.dietTypeTexture?.id ?: 0 else 0
        )
    }
    var expandedTextureDiet by remember { mutableStateOf(false) }

    // Diet type state
    val selectedTypeDietIds = remember {
        mutableStateMapOf<Int, String>().apply {
            if (isExpanded) {
                dietState.dietTypes.forEach { put(it.id, it.description) }
            }
        }
    }
    var expandedTypeDiet by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf<Date?>(null) }
    var dateMeal by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("") }
    var selectIndependent by remember { mutableStateOf("") }
    var selectedProsthesis by remember { mutableStateOf("") }

    // Remote data
    val textureDietOptions = remember { mutableStateListOf<DietTextureTypeState>() }
    val typeDietOptions = remember { mutableStateListOf<DietTypeState>() }
    val dietTextureState = dietRemoteViewModel.remoteDietTexture.value
    val dietTypeState = dietRemoteViewModel.remoteDietType.value

    // Scroll states for dropdowns
    val textureScrollState = rememberScrollState()
    val typeScrollState = rememberScrollState()

    // Load remote data
    LaunchedEffect(Unit) {
        dietRemoteViewModel.getDietTexture()
        dietRemoteViewModel.getDietType()
    }

    // Process texture data when it changes
    LaunchedEffect(dietTextureState) {
        when (dietTextureState) {
            is RemoteApiMessageListDietTexture.Success -> {
                textureDietOptions.clear()
                textureDietOptions.addAll(dietTextureState.message.sortedBy { it.description })
            }

            else -> {
            }
        }
    }

    // Process diet type data when it changes
    LaunchedEffect(dietTypeState) {
        when (dietTypeState) {
            is RemoteApiMessageListDietType.Success -> {
                typeDietOptions.clear()
                typeDietOptions.addAll(dietTypeState.message.sortedBy { it.description })
            }

            else -> {
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HospitalTextField(
            label = stringResource(id = R.string.diet_date_label),
            value = dateMeal,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("""^\d{1,2}$""")) ||
                    newValue.matches(Regex("""^\d{1,2}-$""")) ||
                    newValue.matches(Regex("""^\d{1,2}-\d{1,2}$""")) ||
                    newValue.matches(Regex("""^\d{1,2}-\d{1,2}-$""")) ||
                    newValue.matches(Regex("""^\d{1,2}-\d{1,2}-\d{1,4}$"""))
                ) {
                    dateMeal = newValue

                    date = if (newValue.matches(Regex("""^\d{1,2}-\d{1,2}-\d{4}$"""))) {
                        convertToDate(dateMeal)
                    } else {
                        null
                    }
                }
            },
            placeholder = stringResource(id = R.string.date_format_placeholder),
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = stringResource(id = R.string.select_meal_label),
            options = listOf(
                stringResource(id = R.string.breakfast),
                stringResource(id = R.string.lunch),
                stringResource(id = R.string.dinner)
            ),
            selectedOption = selectedMeal,
            onOptionSelected = { selectedMeal = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Diet texture selection
        Text(
            text = stringResource(id = R.string.texture_type_label), style = TextStyle(
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Medium,
                color = HospitalTheme.TextSecondary
            ), modifier = Modifier.padding(bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Handle different states of diet texture data
        when (dietTextureState) {
            is RemoteApiMessageListDietTexture.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HospitalTheme.Surface)
                        .border(
                            width = 1.dp,
                            color = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = HospitalTheme.Primary, modifier = Modifier.size(24.dp)
                    )
                }
            }

            is RemoteApiMessageListDietTexture.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HospitalTheme.Error.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = HospitalTheme.Error,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.diet_textures_not_available),
                        style = TextStyle(color = HospitalTheme.Error)
                    )
                }
            }

            is RemoteApiMessageListDietTexture.Success -> {
                ExposedDropdownMenuBox(
                    expanded = expandedTextureDiet,
                    onExpandedChange = { expandedTextureDiet = !expandedTextureDiet }) {
                    OutlinedTextField(
                        value = selectedTextureDiet,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text(
                                stringResource(id = R.string.select_texture_type),
                                color = HospitalTheme.TextSecondary.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontSize = 18.sp,
                            color = HospitalTheme.TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HospitalTheme.Primary,
                            unfocusedBorderColor = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
                            cursorColor = HospitalTheme.Primary,
                            focusedContainerColor = HospitalTheme.Surface,
                            unfocusedContainerColor = HospitalTheme.Surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedTextureDiet) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (expandedTextureDiet) "Collapse" else "Expand",
                                tint = HospitalTheme.Primary
                            )
                        })

                    ExposedDropdownMenu(
                        expanded = expandedTextureDiet,
                        onDismissRequest = { expandedTextureDiet = false },
                        modifier = Modifier
                            .background(HospitalTheme.Surface)
                            .clip(RoundedCornerShape(12.dp))
                            .requiredHeightIn(max = 250.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredHeightIn(max = 200.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(textureScrollState)
                            ) {
                                Column {
                                    textureDietOptions.forEach { texture ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    texture.description, style = TextStyle(
                                                        fontSize = 18.sp,
                                                        fontFamily = LatoFontFamily,
                                                        color = if (selectedTextureDiet == texture.description) HospitalTheme.Primary
                                                        else HospitalTheme.TextPrimary
                                                    )
                                                )
                                            }, onClick = {
                                                selectedTextureDiet = texture.description
                                                selectedTextureDietId = texture.id
                                                expandedTextureDiet = false
                                            }, modifier = Modifier.background(
                                                if (selectedTextureDiet == texture.description) HospitalTheme.Primary.copy(
                                                    alpha = 0.1f
                                                )
                                                else HospitalTheme.Surface
                                            )
                                        )
                                    }
                                }
                            }

                            ScrollPositionIndicator(
                                scrollState = textureScrollState,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diet texture selection
        Text(
            text = stringResource(id = R.string.diet_type_label), style = TextStyle(
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Medium,
                color = HospitalTheme.TextSecondary
            ), modifier = Modifier.padding(bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Diet type multi-selection dropdown
        when (dietTypeState) {
            is RemoteApiMessageListDietType.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is RemoteApiMessageListDietType.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HospitalTheme.Error.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = HospitalTheme.Error,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.diet_types_not_available),
                        style = TextStyle(color = HospitalTheme.Error)
                    )
                }
            }

            is RemoteApiMessageListDietType.Success -> {
                ExposedDropdownMenuBox(
                    expanded = expandedTypeDiet,
                    onExpandedChange = { expandedTypeDiet = !expandedTypeDiet }) {
                    OutlinedTextField(
                        value = if (selectedTypeDietIds.isEmpty()) ""
                        else selectedTypeDietIds.values.joinToString(),
                        onValueChange = {},
                        readOnly = true,
                        placeholder = {
                            Text(
                                stringResource(id = R.string.select_diet_type),
                                color = HospitalTheme.TextSecondary.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontSize = 18.sp,
                            color = HospitalTheme.TextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HospitalTheme.Primary,
                            unfocusedBorderColor = HospitalTheme.TextSecondary.copy(alpha = 0.5f),
                            cursorColor = HospitalTheme.Primary,
                            focusedContainerColor = HospitalTheme.Surface,
                            unfocusedContainerColor = HospitalTheme.Surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedTypeDiet) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = if (expandedTypeDiet) "Collapse" else "Expand",
                                tint = HospitalTheme.Primary
                            )
                        })

                    ExposedDropdownMenu(
                        expanded = expandedTypeDiet,
                        onDismissRequest = { expandedTypeDiet = false },
                        modifier = Modifier
                            .background(HospitalTheme.Surface)
                            .clip(RoundedCornerShape(12.dp))
                            .requiredHeightIn(max = 250.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .requiredHeightIn(max = 200.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(typeScrollState)
                            ) {
                                Column {
                                    typeDietOptions.forEach { dietType ->
                                        // Use DropdownMenuItem with checkbox for multi-selection
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Checkbox(
                                                        checked = selectedTypeDietIds.containsKey(
                                                            dietType.id
                                                        ), onCheckedChange = { isChecked ->
                                                            if (isChecked) {
                                                                selectedTypeDietIds[dietType.id] =
                                                                    dietType.description
                                                            } else {
                                                                selectedTypeDietIds.remove(dietType.id)
                                                            }

                                                            // Update parent state with selected types
                                                            val dietTypesList =
                                                                selectedTypeDietIds.entries.map { (id, desc) ->
                                                                    DietTypeState(
                                                                        id = id, description = desc
                                                                    )
                                                                }.toSet()

                                                            onDietStateChange(
                                                                dietState.copy(dietTypes = dietTypesList)
                                                            )
                                                        })
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        dietType.description, style = TextStyle(
                                                            fontSize = 18.sp,
                                                            fontFamily = LatoFontFamily,
                                                            color = if (selectedTypeDietIds.containsKey(
                                                                    dietType.id
                                                                )
                                                            ) HospitalTheme.Primary
                                                            else HospitalTheme.TextPrimary
                                                        )
                                                    )
                                                }
                                            }, onClick = {
                                                // Toggle selection on click
                                                if (selectedTypeDietIds.containsKey(dietType.id)) {
                                                    selectedTypeDietIds.remove(dietType.id)
                                                } else {
                                                    selectedTypeDietIds[dietType.id] =
                                                        dietType.description
                                                }

                                                // Update parent state with selected types
                                                val dietTypesList =
                                                    selectedTypeDietIds.entries.map { (id, desc) ->
                                                        DietTypeState(id = id, description = desc)
                                                    }.toSet()

                                                onDietStateChange(
                                                    dietState.copy(dietTypes = dietTypesList)
                                                )
                                            }, modifier = Modifier.background(
                                                if (selectedTypeDietIds.containsKey(dietType.id)) HospitalTheme.Primary.copy(
                                                    alpha = 0.1f
                                                )
                                                else HospitalTheme.Surface
                                            )
                                        )
                                    }
                                }
                            }
                            // Add custom scroll indicator
                            ScrollPositionIndicator(
                                scrollState = typeScrollState,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = stringResource(id = R.string.patient_autonomy_label),
            options = listOf(
                stringResource(id = R.string.autonomous),
                stringResource(id = R.string.needs_help)
            ),
            selectedOption = selectIndependent,
            onOptionSelected = { selectIndependent = it })

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = stringResource(id = R.string.prosthesis_carrier_label),
            options = listOf(
                stringResource(id = R.string.yes),
                stringResource(id = R.string.no)
            ),
            selectedOption = selectedProsthesis,
            onOptionSelected = { selectedProsthesis = it })
        val needHelp = stringResource(id = R.string.needs_help)
        val yes = stringResource(id = R.string.yes)
        // Update state when any relevant field changes
        LaunchedEffect(
            date,
            selectedMeal,
            selectedTextureDiet,
            selectedTextureDietId,
            selectedTypeDietIds.size,
            selectIndependent,
            selectedProsthesis
        ) {
            val dietTypesList = selectedTypeDietIds.entries.map { (id, desc) ->
                DietTypeState(id = id, description = desc)
            }.toSet()

            val updatedDietState = DietState(
                id = dietState.id,
                date = date,
                takeData = if (selectedMeal.isNotEmpty()) selectedMeal else null,
                dietTypes = dietTypesList,
                dietTypeTexture = if (selectedTextureDiet.isNotEmpty()) {
                    DietTextureTypeState(
                        id = selectedTextureDietId, description = selectedTextureDiet
                    )
                } else {
                    null
                },
                independent = if (selectIndependent.isNotEmpty()) {
                    if (selectIndependent == needHelp) 1 else 0
                } else {
                    null
                },
                prosthesis = if (selectedProsthesis.isNotEmpty()) {
                    if (selectedProsthesis == yes) 1 else 0
                } else {
                    null
                }
            )
            onDietStateChange(updatedDietState)
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun convertToDate(inputDate: String): Date {
    val inputFormat = SimpleDateFormat("dd-MM-yyyy")
    return inputFormat.parse(inputDate)
}


@Composable
fun EnhancedSaveButton(
    text: String, isEnabled: Boolean, fontFamily: FontFamily = LatoFontFamily, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = HospitalTheme.Primary.copy(alpha = 0.3f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = HospitalTheme.SaveColor,
            contentColor = Color.White,
            disabledContainerColor = HospitalTheme.BackgroundMuted,
            disabledContentColor = HospitalTheme.TextSecondary
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text, style = TextStyle(
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = NunitoFontFamily
                )
            )
        }

        AnimatedVisibility(visible = !isEnabled) {
            Text(
                text = stringResource(id = R.string.button_save_create), style = TextStyle(
                    fontSize = 14.sp, fontFamily = fontFamily
                ), modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}