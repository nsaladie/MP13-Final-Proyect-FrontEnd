package com.example.hospitalfrontend.ui.nurses.view

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.*
import com.example.hospitalfrontend.network.*
import com.example.hospitalfrontend.ui.nurses.viewmodels.*
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

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            HospitalTopAppBar(
                title = "CREAR NOVA CURA", onCloseClick = { navController.popBackStack() })
        }, bottomBar = {
            HospitalBottomBar(
                text = "Desar canvis",
                isEnabled = isFormValid,
                fontFamily = HospitalTheme.latoBoldFont
            ) {
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

                FormSection(title = "Dieta", icon = Icons.Outlined.Restaurant) {
                    DietSection(
                        dietState = dietState,
                        onDietStateChange = { dietState = it },
                        dietRemoteViewModel = dietRemoteViewModel
                    )
                }

                FormSection(title = "Drenatges", icon = Icons.Outlined.MedicalServices) {
                    DrainSection(onDrainStateChange = { drainState = it })
                }

                FormSection(title = "Higiene", icon = Icons.Outlined.CleanHands) {
                    HygieneSelection(onHygieneStateChange = { hygieneState = it })
                }

                FormSection(title = "Mobilitzacions", icon = Icons.Outlined.AccessibilityNew) {
                    MobilizationSection(onMobilizationStateChange = { mobilizationState = it })
                }

                FormSection(title = "Observacions", icon = Icons.Outlined.ContentPasteGo) {
                    ObservationsField(
                        value = observation, onValueChange = { observation = it })
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        LaunchedEffect(remoteApiMessage) {
            when (remoteApiMessage) {
                is RemoteApiMessageBoolean.Success -> {
                    dialogMessage = "Nova cura creada correctament"
                    showSuccessDialog = true
                    patientRemoteViewModel.clearApiMessage()
                }

                is RemoteApiMessageBoolean.Error -> {
                    dialogMessage =
                        "Hi ha hagut una error en la creació de la nova cura, si us plau intenta-ho de nou"
                    showErrorDialog = true
                    patientRemoteViewModel.clearApiMessage()
                }

                RemoteApiMessageBoolean.Loading -> {

                }
            }
        }

        // Dialog management
        if (showSuccessDialog) {
            HospitalAlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = "Èxit",
                text = dialogMessage,
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Acceptar")
                    }
                })
        }

        if (showErrorDialog) {
            HospitalAlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = "Error",
                text = dialogMessage,
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Acceptar")
                    }
                })
        }
    }
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
            .imePadding()
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
fun HospitalAlertDialog(
    onDismissRequest: () -> Unit, title: String, text: String, confirmButton: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = confirmButton,
        containerColor = HospitalTheme.Surface
    )
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
            VitalSignsHeader(title = "Constants Vitals", icon = Icons.Outlined.MonitorHeart)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HospitalTheme.ColumColor.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tensió Arterial *", style = TextStyle(
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
                        label = "Sistólica",
                        placeholder = "mmHg",
                        modifier = Modifier.weight(1f)
                    )

                    VitalSignNumberField(
                        value = diastolic,
                        onValueChange = { diastolic = validateDecimal(it, diastolic) },
                        label = "Diastólica",
                        placeholder = "mmHg",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            VitalSignMeasurementField(
                label = "Freqüència Respiratòria *",
                value = respiratoryRate,
                onValueChange = { respiratoryRate = validateDecimal(it, respiratoryRate) },
                unit = "x'"
            )

            VitalSignMeasurementField(
                label = "Pols *",
                value = pulse,
                onValueChange = { pulse = validateDecimal(it, pulse) },
                unit = "x'"
            )

            VitalSignMeasurementField(
                label = "Temperatura *",
                value = temperature,
                onValueChange = { temperature = validateDecimal(it, temperature) },
                unit = "°C"
            )

            VitalSignMeasurementField(
                label = "Saturació O₂ *",
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
                text = "Mesures Addicionals", style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = NunitoFontFamily,
                    color = HospitalTheme.TextPrimary
                ), modifier = Modifier.padding(bottom = 16.dp)
            )

            VitalSignMeasurementField(
                label = "Volum d'orina",
                value = urineVolume,
                onValueChange = { urineVolume = validateDecimal(it, urineVolume) },
                unit = "mL"
            )

            VitalSignMeasurementField(
                label = "Moviments intestinals",
                value = bowelMovements,
                onValueChange = { bowelMovements = validateDecimal(it, bowelMovements) },
                unit = "mL"
            )

            VitalSignMeasurementField(
                label = "Terapia amb sèrum",
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
            label = "Tipus de Drenatge *",
            value = drainType,
            onValueChange = { drainType = it },
            placeholder = "Ex: Penrose, Jackson-Pratt..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        HospitalTextField(label = "Quantitat de Dèbit *", value = drainOutput, onValueChange = {
            drainOutput = it
        }, placeholder = "mL", trailingIcon = {
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
    val options = listOf("Allitat", "Parcial al llit", "Dutxa amb ajuda", "Autònom")
    var selectedOption by rememberSaveable { mutableStateOf("") }

    EnhancedRadioGroup(
        title = "Tipus d'higiene *",
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        HospitalTextField(
            label = "Sedestació *", value = sedestation, onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]+$"))) {
                    sedestation = newValue
                }
            }, placeholder = "Introdueix un valor entre 1 i 10"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = "Deambulació *",
            options = listOf("Sense ajuda", "Amb ajuda"),
            selectedOption = walkingAssis,
            onOptionSelected = {
                walkingAssis = it
                assisDesc = if (it == "Amb ajuda") assisDesc else ""
            }) {
            // Only show assistance options when "Amb ajuda" is selected
            if (walkingAssis == "Amb ajuda") {
                Spacer(modifier = Modifier.height(12.dp))
                EnhancedRadioGroup(
                    title = "Tipus d'ajuda:",
                    options = listOf("Bastó", "Caminador", "Ajuda Física"),
                    selectedOption = assisDesc,
                    onOptionSelected = { assisDesc = it },
                    haveBorder = false,
                    hasBackground = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HospitalTextField(
            label = "Canvis Posturals *", value = changes, onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    changes = newValue
                }
            }, placeholder = "Quantitat"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = "Decúbits *",
            options = listOf("Supí", "Lateral E", "Lateral D"),
            selectedOption = decubitus,
            onOptionSelected = { decubitus = it })
    }

    // Update state when any field changes
    LaunchedEffect(sedestation, walkingAssis, assisDesc, changes, decubitus) {
        val mobilizationState = MobilizationState(
            sedestation = sedestation.toIntOrNull(),
            walkingAssis = if (walkingAssis.isNotEmpty()) {
                if (walkingAssis == "Amb ajuda") 1 else 0
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
    // Diet texture state
    var selectedTextureDiet by remember {
        mutableStateOf(
            dietState.dietTypeTexture?.description ?: ""
        )
    }
    var selectedTextureDietId by remember { mutableIntStateOf(dietState.dietTypeTexture?.id ?: 0) }
    var expandedTextureDiet by remember { mutableStateOf(false) }

    // Diet type state
    val selectedTypeDietIds = remember {
        mutableStateMapOf<Int, String>().apply {
            dietState.dietTypes.forEach { put(it.id, it.description) }
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
            label = "Data de la dieta *",
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
            placeholder = "dd-MM-YYYY",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = "Selecciona el menjar *",
            options = listOf("Esmorzar", "Dinar", "Sopar"),
            selectedOption = selectedMeal.toString(),
            onOptionSelected = { selectedMeal = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Diet texture selection
        Text(
            text = "Tipus de textura *", style = TextStyle(
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
                        text = "Textures de dietas no disponibles",
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
                                "Selecciona un tipus de textura",
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
            text = "Tipus de dieta *", style = TextStyle(
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
                        text = "Tipus de dietas no disponibles",
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
                                "Selecciona un tipus de dieta",
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
            title = "Autonomia del paciente *",
            options = listOf("Autònom", "Ayuda"),
            selectedOption = selectIndependent,
            onOptionSelected = { selectIndependent = it })

        Spacer(modifier = Modifier.height(16.dp))

        EnhancedRadioGroup(
            title = "Portador de prótesis *",
            options = listOf("Sí", "No"),
            selectedOption = selectedProsthesis,
            onOptionSelected = { selectedProsthesis = it })

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
                    if (selectIndependent == "Ayuda") 1 else 0
                } else {
                    null
                },
                prosthesis = if (selectedProsthesis.isNotEmpty()) {
                    if (selectedProsthesis == "Sí") 1 else 0
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
                text = "(Completi els camps obligatoris)", style = TextStyle(
                    fontSize = 14.sp, fontFamily = fontFamily
                ), modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}