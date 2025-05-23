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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.data.remote.viewmodel.DiagnosisRemoteViewModel
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDiagnosisScreen(
    navController: NavHostController,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    patientId: Int,
    auxiliaryViewModel: AuxiliaryViewModel,
    isError: MutableState<Boolean>,
) {

    val vesicalInfo = rememberSaveable { mutableStateOf("") }
    val rectalInfo = rememberSaveable { mutableStateOf("") }
    val nasogastricInfo = rememberSaveable { mutableStateOf("") }

    val customPrimaryColor = Color(0xFFA9C7C7)
    val grauOptions = GrauOptions()
    val selectedGrau = rememberSaveable { mutableStateOf("") }

    val oxygenRequired = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val oxygenType = rememberSaveable { mutableStateOf("") }

    val diaperRequired = rememberSaveable { mutableStateOf<Boolean?>(null) }
    val numberOfChanges = rememberSaveable { mutableStateOf("") }
    val skinCondition = rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val createDiagnosisError = remember { mutableStateOf(false) }
    val auxiliary = auxiliaryViewModel.getAuxiliaryState()
    val latoLightFont = FontFamily(Font(R.font.lato_light))
    val messageApi = diagnosisRemoteViewModel.remoteApiMessageDiagnosis.value

    val isFormValidState = remember { mutableStateOf(false) }

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
            vesicalInfo.value,
            nasogastricInfo.value,
            rectalInfo.value
        )
    }

    LaunchedEffect(Unit) {
        createDiagnosisError.value = false
        isError.value = false
        diagnosisRemoteViewModel.clearApiMessage()
    }

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
                createDiagnosisError.value = true
                isLoading = false
            }

            RemoteApiMessageDiagnosis.Idle -> {
                isLoading = false
            }

        }
    }
    DisposableEffect(Unit) {
        onDispose {
            diagnosisRemoteViewModel.clearApiMessage()
        }
    }
    if (createDiagnosisError.value) {
        AlertDialog(onDismissRequest = {
            createDiagnosisError.value = false
            diagnosisRemoteViewModel.clearApiMessage()
        }, confirmButton = {
            TextButton(onClick = {
                createDiagnosisError.value = false
                diagnosisRemoteViewModel.clearApiMessage()
            }) {
                Text("OK")
            }
        }, title = {
            Text(
                text = stringResource(id = R.string.diagnosis_create_error_title),
                color = Color.Red,
                style = TextStyle(
                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = stringResource(id = R.string.diagnosis_create_error_message),
                style = TextStyle(
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
                            text = stringResource(id = R.string.diagnosis_create_title),
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }, navigationIcon = {

                    IconButton(onClick = {
                        diagnosisRemoteViewModel.clearApiMessage()
                        isError.value = false
                        navController.popBackStack()
                    }) {
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
                .background(customPrimaryColor)
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
                    .padding(16.dp)
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
                    vesicalInfo = vesicalInfo

                )

                Button(
                    onClick = {
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
                            diagnosisRemoteViewModel.createDiagnosis(registerState, diagnosisState)
                        }
                    },
                    enabled = isFormValidState.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(151, 199, 150),
                        disabledContainerColor = Color.LightGray
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
                            text = stringResource(id = R.string.button_save),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = latoLightFont,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GrauOptions(): List<String> {
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
    rectalInfo: MutableState<String>
) {
    val customIconColor = Color(0xFF505050)

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

            LevelofDependency(
                labelRes = R.string.degree_dependence,
                options = options,
                selectedOption = selectedOption.value,
                onOptionSelected = { selectedOption.value = it },
                icon = Icons.Filled.Accessibility,
                iconColor = customIconColor,
            )
            OxygenInfoItem(
                labelRes = R.string.oxygen,
                icon = Icons.Filled.Air,
                iconColor = customIconColor,
                oxygenRequired = oxygenRequired,
                oxygenType = oxygenType.value,
                onOxygenRequiredChange = { oxygenRequired.value = it },
                onOxygenTypeChange = { oxygenType.value = it })

            DiaperInfoItem(
                label = R.string.diaper_label,
                icon = Icons.Filled.Science,
                iconColor = customIconColor,
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
                iconColor = customIconColor
            )

            ItemInfo(
                labelResId = R.string.rectal_probe,
                info = rectalInfo.value,
                onInfoChange = { rectalInfo.value = it },
                icon = Icons.Filled.Medication,
                iconColor = customIconColor
            )

            ItemInfo(
                labelResId = R.string.nasogastric_probe,
                info = nasalInfo.value,
                onInfoChange = { nasalInfo.value = it },
                icon = Icons.Filled.HealthAndSafety,
                iconColor = customIconColor
            )

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
    iconColor: Color = Color(0xFF505050),
) {
    val label = stringResource(id = labelRes)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                color = Color(0xFF2C3E50),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        options.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == level,
                    onClick = { onOptionSelected(level) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                )
                Text(
                    text = level,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontFamily = LatoFontFamily
                )
            }
        }
    }
}

@Composable
fun OxygenInfoItem(
    @StringRes labelRes: Int,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        }

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            ) {
                RadioButton(
                    selected = oxygenRequired.value == (option == yesOption),
                    onClick = { onOxygenRequiredChange(option == yesOption) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                )

                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontFamily = LatoFontFamily
                )
            }
        }

        if (oxygenRequired.value == true) {
            TextField(
                value = oxygenType,
                onValueChange = onOxygenTypeChange,
                label = { Text(text = stringResource(id= R.string.oxygen_type)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }
}


@Composable
fun DiaperInfoItem(
    label: Int,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = label),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF2C3E50)
            )
        }

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            ) {
                RadioButton(
                    selected = diaperRequired.value == (option == yesOption),
                    onClick = { onDiaperRequiredChange(option == yesOption) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                )

                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 18.sp,
                    fontFamily = LatoFontFamily
                )
            }
        }

        if (diaperRequired.value == true) {
            TextField(
                value = numberOfChanges,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        onNumberOfChangesChange(newValue)
                    }
                },
                label = { Text(stringResource(R.string.number_changes)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            TextField(
                value = skinCondition,
                onValueChange = onSkinConditionChange,
                label = { Text(stringResource(R.string.skin_status)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
    iconColor: Color = Color(0xFF505050),
) {
    val label = stringResource(id = labelResId)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {

            TextField(
                value = info,
                onValueChange = onInfoChange,
                label = { Text(stringResource(R.string.type_here)) },
                modifier = Modifier.fillMaxWidth(),

                )
        }
    }
}

fun isFormValid(
    selectedGrau: String,
    oxygenRequired: Boolean?,
    oxygenType: String,
    diaperRequired: Boolean?,
    numberOfChanges: String,
    skinCondition: String,
    vesicalInfo: String,
    nasogastricInfo: String,
    rectalInfo: String
): Boolean {
    return selectedGrau.isNotEmpty() &&
            (oxygenRequired == false || (oxygenRequired == true && oxygenType.isNotEmpty())) &&
            (diaperRequired == false || (diaperRequired == true && numberOfChanges.isNotEmpty() && skinCondition.isNotEmpty())) &&
            vesicalInfo.isNotEmpty() &&
            nasogastricInfo.isNotEmpty() &&
            rectalInfo.isNotEmpty()
}