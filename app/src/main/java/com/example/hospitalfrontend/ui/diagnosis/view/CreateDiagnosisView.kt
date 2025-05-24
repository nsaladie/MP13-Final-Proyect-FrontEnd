package com.example.hospitalfrontend.ui.diagnosis.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.data.remote.viewmodel.DiagnosisRemoteViewModel
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel

object DiagnosisColors {
    val Primary = Color(0xFFA9C7C7)
    val Secondary = Color(0xFF2ECC71)
    val TextPrimary = Color(0xFF2C3E50)
    val TextSecondary = Color(0xFFC4C4C4)
    val BorderUnfocused = Color(0xFFBDC3C7)
    val IconColor = Color(0xFF505050)
    val White = Color.White
}

@Composable
fun CreateDiagnosisScreen(
    navController: NavHostController,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    patientId: Int,
    auxiliaryViewModel: AuxiliaryViewModel,
    isError: MutableState<Boolean>,
) {
    // Form state
    val vesicalInfo = rememberSaveable { mutableStateOf("") }
    val rectalInfo = rememberSaveable { mutableStateOf("") }
    val nasogastricInfo = rememberSaveable { mutableStateOf("") }
    val grauOptions = grauOptions()
    val selectedGrau = rememberSaveable { mutableStateOf("") }
    val oxygenRequired = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val oxygenType = rememberSaveable { mutableStateOf("") }
    val diaperRequired = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val numberOfChanges = rememberSaveable { mutableStateOf("") }
    val skinCondition = rememberSaveable { mutableStateOf("") }

    // UI state
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val auxiliary = auxiliaryViewModel.getAuxiliaryState()
    val messageApi = diagnosisRemoteViewModel.remoteApiMessageDiagnosis.value

    val isFormValidState = remember { mutableStateOf(false) }

    // Form validation
    LaunchedEffect(
        selectedGrau.value,
        oxygenRequired.value,
        oxygenType.value,
        diaperRequired.value,
        numberOfChanges.value,
        skinCondition.value,
        vesicalInfo.value,
        nasogastricInfo.value,
        rectalInfo.value
    ) {
        isFormValidState.value = isFormValid(
            selectedGrau.value,
            oxygenRequired.value,
            oxygenType.value,
            diaperRequired.value,
            numberOfChanges.value,
            skinCondition.value,
        )
    }

    // Initialize
    LaunchedEffect(Unit) {
        isError.value = false
        diagnosisRemoteViewModel.clearApiMessage()
    }

    // Handle API responses
    LaunchedEffect(messageApi) {
        when (messageApi) {
            is RemoteApiMessageDiagnosis.SuccessCreation -> {
                diagnosisRemoteViewModel.clearApiMessage()
                isLoading = false
                navController.popBackStack()
            }

            RemoteApiMessageDiagnosis.Loading -> {
                isLoading = true
            }

            RemoteApiMessageDiagnosis.Error -> {
                isLoading = false
                showErrorDialog = true
                diagnosisRemoteViewModel.clearApiMessage()
            }

            RemoteApiMessageDiagnosis.Idle -> {
                isLoading = false
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            diagnosisRemoteViewModel.clearApiMessage()
        }
    }

    // Error Dialog
    if (showErrorDialog) {
        DiagnosisStatusDialog(
            title = stringResource(id = R.string.diagnosis_create_error_title),
            message = stringResource(id = R.string.diagnosis_create_error_message),
            icon = Icons.Filled.Error,
            iconTint = Color.Red,
            onDismiss = {
                showErrorDialog = false
            }
        )
    }

    Scaffold(
        containerColor = DiagnosisColors.Primary,
        topBar = {
            DiagnosisTopBar(
                title = stringResource(id = R.string.diagnosis_create_title),
                onNavigateBack = {
                    diagnosisRemoteViewModel.clearApiMessage()
                    isError.value = false
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DiagnosisColors.Primary)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = DiagnosisColors.White,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    CreateDiagnosisDetailsCard(
                        options = grauOptions,
                        selectedOption = selectedGrau,
                        oxygenRequired = oxygenRequired,
                        oxygenType = oxygenType,
                        diaperRequired = diaperRequired,
                        numberOfChanges = numberOfChanges,
                        skinCondition = skinCondition,
                        nasalInfo = nasogastricInfo,
                        rectalInfo = rectalInfo,
                        vesicalInfo = vesicalInfo,
                        isFormValid = isFormValidState.value,
                        onSave = {
                            val diagnosisState = DiagnosisState(
                                id = 0,
                                oxygenLevel = if (oxygenRequired.value == true) 1 else 0,
                                dependencyLevel = selectedGrau.value,
                                oxygenLevelDescription = oxygenType.value,
                                diapers = diaperRequired.value == true,
                                totalChangesDiapers = numberOfChanges.value.toIntOrNull() ?: 0,
                                detailDescription = skinCondition.value,
                                urinaryCatheter = vesicalInfo.value,
                                rectalCatheter = rectalInfo.value,
                                nasogastricTube = nasogastricInfo.value
                            )

                            val registerState = auxiliary?.let {
                                RegisterState(
                                    id = 0,
                                    auxiliary = it,
                                    date = null,
                                    patient = PatientState(patientId),
                                    hygieneType = null,
                                    diet = null,
                                    drain = null,
                                    mobilization = null,
                                    vitalSign = null,
                                    observation = null
                                )
                            }
                            isError.value = false
                            if (registerState != null) {
                                diagnosisRemoteViewModel.createDiagnosis(
                                    registerState,
                                    diagnosisState
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisTopBar(
    title: String,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoFontFamily,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = DiagnosisColors.TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DiagnosisColors.Primary,
            scrolledContainerColor = DiagnosisColors.Primary
        )
    )
}

@Composable
fun DiagnosisStatusDialog(
    title: String,
    message: String,
    icon: ImageVector,
    iconTint: Color,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DiagnosisColors.White),
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
                        color = DiagnosisColors.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 18.sp,
                        color = DiagnosisColors.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DiagnosisColors.Primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.dialog_ok),
                        style = TextStyle(
                            fontFamily = LatoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun grauOptions(): List<String> {
    return listOf(
        stringResource(id = R.string.degree_independent),
        stringResource(id = R.string.degree_partially_dependent),
        stringResource(id = R.string.degree_totally_dependent)
    )
}

@Composable
fun CreateDiagnosisDetailsCard(
    options: List<String>,
    selectedOption: MutableState<String>,
    oxygenRequired: MutableState<Boolean?>,
    oxygenType: MutableState<String>,
    diaperRequired: MutableState<Boolean?>,
    numberOfChanges: MutableState<String>,
    skinCondition: MutableState<String>,
    vesicalInfo: MutableState<String>,
    nasalInfo: MutableState<String>,
    rectalInfo: MutableState<String>,
    isFormValid: Boolean,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DiagnosisColors.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            LevelofDependency(
                labelRes = R.string.degree_dependence,
                options = options,
                selectedOption = selectedOption.value,
                onOptionSelected = { selectedOption.value = it },
                icon = Icons.Filled.Accessibility,
                iconColor = DiagnosisColors.IconColor,
            )

            OxygenInfoItem(
                labelRes = R.string.oxygen,
                icon = Icons.Filled.Air,
                iconColor = DiagnosisColors.IconColor,
                oxygenRequired = oxygenRequired,
                oxygenType = oxygenType.value,
                onOxygenRequiredChange = { oxygenRequired.value = it },
                onOxygenTypeChange = { oxygenType.value = it }
            )

            DiaperInfoItem(
                label = R.string.diaper_label,
                icon = Icons.Filled.Science,
                iconColor = DiagnosisColors.IconColor,
                diaperRequired = diaperRequired,
                numberOfChanges = numberOfChanges.value,
                skinCondition = skinCondition.value,
                onDiaperRequiredChange = { diaperRequired.value = it },
                onNumberOfChangesChange = { numberOfChanges.value = it },
                onSkinConditionChange = { skinCondition.value = it }
            )

            ItemInfo(
                labelResId = R.string.vesical_probe,
                info = vesicalInfo.value,
                onInfoChange = { vesicalInfo.value = it },
                icon = Icons.Filled.Water,
                iconColor = DiagnosisColors.IconColor
            )

            ItemInfo(
                labelResId = R.string.rectal_probe,
                info = rectalInfo.value,
                onInfoChange = { rectalInfo.value = it },
                icon = Icons.Filled.Medication,
                iconColor = DiagnosisColors.IconColor
            )

            ItemInfo(
                labelResId = R.string.nasogastric_probe,
                info = nasalInfo.value,
                onInfoChange = { nasalInfo.value = it },
                icon = Icons.Filled.HealthAndSafety,
                iconColor = DiagnosisColors.IconColor
            )

            // Botón de guardar dentro del Card
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSave,
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DiagnosisColors.Secondary,
                    disabledContainerColor = DiagnosisColors.Secondary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = DiagnosisColors.White
                    )
                    Text(
                        text = stringResource(id = R.string.button_save),
                        style = TextStyle(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DiagnosisColors.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LevelofDependency(
    @StringRes labelRes: Int,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    iconColor: Color = DiagnosisColors.IconColor,
) {
    val label = stringResource(id = labelRes)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiagnosisColors.TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        options.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                RadioButton(
                    selected = selectedOption == level,
                    onClick = { onOptionSelected(level) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = DiagnosisColors.Primary,
                        unselectedColor = DiagnosisColors.BorderUnfocused
                    )
                )
                Text(
                    text = level,
                    modifier = Modifier.padding(start = 12.dp),
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 16.sp,
                        color = DiagnosisColors.TextPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun OxygenInfoItem(
    @StringRes labelRes: Int,
    icon: ImageVector,
    iconColor: Color = DiagnosisColors.IconColor,
    oxygenType: String,
    oxygenRequired: MutableState<Boolean?>,
    onOxygenRequiredChange: (Boolean?) -> Unit,
    onOxygenTypeChange: (String) -> Unit
) {
    val label = stringResource(id = labelRes)
    val yesOption = stringResource(id = R.string.yes)
    val noOption = stringResource(id = R.string.no)
    val options = listOf(yesOption, noOption)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiagnosisColors.TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                RadioButton(
                    selected = oxygenRequired.value == (option == yesOption),
                    onClick = { onOxygenRequiredChange(option == yesOption) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = DiagnosisColors.Primary,
                        unselectedColor = DiagnosisColors.BorderUnfocused
                    )
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 12.dp),
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 18.sp,
                        color = DiagnosisColors.TextPrimary
                    )
                )
            }
        }

        if (oxygenRequired.value == true) {
            Spacer(modifier = Modifier.height(8.dp))

            InputTextDiagnosis(
                stringResource(id = R.string.oxygen_type),
                oxygenType,
                onOxygenTypeChange
            )
        }
    }
}

@Composable
fun InputTextDiagnosis(
    label: String,
    inputText: String,
    onInputTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = inputText,
        onValueChange = onInputTextChange,
        label = {
            Text(
                text = label,
                color = DiagnosisColors.TextSecondary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DiagnosisColors.Primary,
            unfocusedBorderColor = DiagnosisColors.BorderUnfocused,
            focusedContainerColor = DiagnosisColors.White,
            unfocusedContainerColor = DiagnosisColors.White
        ),
        textStyle = TextStyle(
            fontFamily = LatoFontFamily,
            fontSize = 18.sp,
            color = DiagnosisColors.TextPrimary
        ),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun DiaperInfoItem(
    label: Int,
    icon: ImageVector,
    iconColor: Color = DiagnosisColors.IconColor,
    diaperRequired: MutableState<Boolean?>,
    numberOfChanges: String,
    skinCondition: String,
    onDiaperRequiredChange: (Boolean?) -> Unit,
    onNumberOfChangesChange: (String) -> Unit,
    onSkinConditionChange: (String) -> Unit
) {
    val yesOption = stringResource(id = R.string.yes)
    val noOption = stringResource(id = R.string.no)
    val options = listOf(yesOption, noOption)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = label),
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiagnosisColors.TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                RadioButton(
                    selected = diaperRequired.value == (option == yesOption),
                    onClick = { onDiaperRequiredChange(option == yesOption) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = DiagnosisColors.Primary,
                        unselectedColor = DiagnosisColors.BorderUnfocused
                    )
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 12.dp),
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 18.sp,
                        color = DiagnosisColors.TextPrimary
                    )
                )
            }
        }

        if (diaperRequired.value == true) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = numberOfChanges,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        onNumberOfChangesChange(newValue)
                    }
                },
                label = {
                    Text(
                        text = stringResource(R.string.number_changes),
                        color = DiagnosisColors.TextSecondary
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DiagnosisColors.Primary,
                    unfocusedBorderColor = DiagnosisColors.BorderUnfocused,
                    focusedContainerColor = DiagnosisColors.White,
                    unfocusedContainerColor = DiagnosisColors.White
                ),
                textStyle = TextStyle(
                    fontFamily = LatoFontFamily,
                    fontSize = 18.sp,
                    color = DiagnosisColors.TextPrimary
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            InputTextDiagnosis(
                stringResource(R.string.skin_status),
                skinCondition,
                onSkinConditionChange
            )
        }
    }
}

@Composable
fun ItemInfo(
    @StringRes labelResId: Int,
    info: String,
    onInfoChange: (String) -> Unit,
    icon: ImageVector,
    iconColor: Color = DiagnosisColors.IconColor,
) {
    val label = stringResource(id = labelResId)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiagnosisColors.TextPrimary
                )
            )

        }
        InputTextDiagnosis(stringResource(R.string.type_here), info, onInfoChange)
    }
}

fun isFormValid(
    selectedGrau: String,
    oxygenRequired: Boolean?,
    oxygenType: String,
    diaperRequired: Boolean?,
    numberOfChanges: String,
    skinCondition: String,
): Boolean {
    return selectedGrau.isNotEmpty() &&
            (oxygenRequired == false || (oxygenRequired == true && oxygenType.isNotEmpty())) &&
            (diaperRequired == false || (diaperRequired == true && numberOfChanges.isNotEmpty() && skinCondition.isNotEmpty()))
}