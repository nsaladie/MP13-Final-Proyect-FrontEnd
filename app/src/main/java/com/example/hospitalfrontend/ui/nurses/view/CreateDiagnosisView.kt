package com.example.hospitalfrontend.ui.nurses.view

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.DiagnosisState
import com.example.hospitalfrontend.model.RegisterState
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.model.AuxiliaryState
import com.example.hospitalfrontend.model.VitalSignState
import com.example.hospitalfrontend.network.DiagnosisRemoteViewModel
import com.example.hospitalfrontend.network.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.network.RemoteApiMessageNurse
import com.example.hospitalfrontend.ui.nurses.viewmodels.DiagnosisViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDiagnosisScreen(
    navController: NavHostController,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    patientId: Int,
    auxiliaryViewModel: AuxiliaryViewModel,
    isError: MutableState<Boolean>,
) {

    val vesicalInfo = rememberSaveable { mutableStateOf("") }
    val rectalInfo = rememberSaveable { mutableStateOf("") }
    val nasogastricInfo = rememberSaveable { mutableStateOf("") }

    val customPrimaryColor = Color(0xFFA9C7C7)
    val grauOptions = listOf("Autònom AVD", "Depenent parcial AVD", "Depenent total")
    val selectedGrau = rememberSaveable { mutableStateOf(grauOptions[0]) }

    val oxygenRequired = rememberSaveable { mutableStateOf(false) }
    val oxygenType = rememberSaveable { mutableStateOf("") }

    val diaperRequired = rememberSaveable { mutableStateOf(false) }
    val numberOfChanges = rememberSaveable { mutableStateOf("") }
    val skinCondition = rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val createDiagnosisError = remember { mutableStateOf(false) }
    val auxiliary = auxiliaryViewModel.getAuxiliaryState()
    val latoLightFont = FontFamily(Font(R.font.lato_light))
    val messageApi = diagnosisRemoteViewModel.remoteApiMessageDiagnosis.value

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
                text = "Error: Create Diagnostic", color = Color.Red, style = TextStyle(
                    fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = "Failed to create diagnostic", style = TextStyle(
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
                        text = "CREAR DIAGNÓSTIC", style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
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
                Icon(
                    Icons.Filled.LocalHospital,
                    contentDescription = "Diagnòstic",
                    tint = Color.Black,
                    modifier = Modifier.padding(end = 16.dp)
                )
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
                            oxygenLevel = if (oxygenRequired.value) 1 else 0,
                            dependencyLevel = selectedGrau.value,
                            oxygenLevelDescription = oxygenType.value,
                            diapers = diaperRequired.value,
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
                            "Guardar",
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
fun CreateDiagnosisDetailsCard(
    options: List<String>,
    selectedOption: MutableState<String>,
    oxygenRequired: MutableState<Boolean>,
    oxygenType: MutableState<String>,
    diaperRequired: MutableState<Boolean>,
    numberOfChanges: MutableState<String>,
    skinCondition: MutableState<String>,
    vesicalInfo: MutableState<String>,
    nasalInfo: MutableState<String>,
    rectalInfo: MutableState<String>
) {
    val customPrimaryColor = Color(0xFFA9C7C7)
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
                label = "Grau de dependència",
                options = options,
                selectedOption = selectedOption.value,
                onOptionSelected = { selectedOption.value = it },
                icon = Icons.Filled.Accessibility,
                iconColor = customIconColor,
            )
            OxygenInfoItem(
                label = "Oxigen",
                icon = Icons.Filled.Air,
                iconColor = customIconColor,
                oxygenRequired = oxygenRequired,
                oxygenType = oxygenType.value,
                onOxygenRequiredChange = { oxygenRequired.value = it },
                onOxygenTypeChange = { oxygenType.value = it })

            DiaperInfoItem(
                label = "Portador de bolquer",
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
                label = "Sonda Vesical",
                info = vesicalInfo.value,
                onInfoChange = { vesicalInfo.value = it },
                icon = Icons.Filled.Water,
                iconColor = customIconColor
            )



            ItemInfo(
                label = "Sonda Rectal",
                info = rectalInfo.value,
                onInfoChange = { rectalInfo.value = it },
                icon = Icons.Filled.Medication,
                iconColor = customIconColor
            )



            ItemInfo(
                label = "Sonda Nasogàstrica",
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
    label: String,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    iconColor: Color = Color(0xFF505050),
) {
    val infoFontSize = 18.sp
    val labelFontSize = 20.sp
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
                text = label, style = MaterialTheme.typography.titleMedium
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
                    text = level, modifier = Modifier.padding(start = 8.dp), fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun OxygenInfoItem(
    label: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    oxygenType: String,
    oxygenRequired: MutableState<Boolean>,
    onOxygenRequiredChange: (Boolean) -> Unit,
    onOxygenTypeChange: (String) -> Unit


) {
    val infoFontSize = 18.sp
    val labelFontSize = 20.sp
    val options = listOf("Sí", "No")

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
                text = label, style = MaterialTheme.typography.titleMedium
            )
        }

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            ) {
                RadioButton(
                    selected = if (option == "Sí") oxygenRequired.value else !oxygenRequired.value,
                    onClick = { onOxygenRequiredChange(option == "Sí") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                )

                Text(
                    text = option, modifier = Modifier.padding(start = 8.dp), fontSize = 16.sp
                )
            }
        }

        if (oxygenRequired.value) {
            TextField(
                value = oxygenType,
                onValueChange = onOxygenTypeChange,
                label = { Text("Tipus d'oxigen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

    }
}

@Composable
fun DiaperInfoItem(
    label: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    diaperRequired: MutableState<Boolean>,
    numberOfChanges: String,
    skinCondition: String,
    onDiaperRequiredChange: (Boolean) -> Unit,
    onNumberOfChangesChange: (String) -> Unit,
    onSkinConditionChange: (String) -> Unit
) {
    val options = listOf("Sí", "No")

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
                text = label, style = MaterialTheme.typography.titleMedium
            )
        }

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, bottom = 4.dp)
            ) {
                RadioButton(
                    selected = if (option == "Sí") diaperRequired.value else !diaperRequired.value,
                    onClick = { onDiaperRequiredChange(option == "Sí") },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Blue)
                )

                Text(
                    text = option, modifier = Modifier.padding(start = 8.dp), fontSize = 16.sp
                )
            }
        }

        if (diaperRequired.value) {
            TextField(
                value = numberOfChanges,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        onNumberOfChangesChange(newValue)
                    }
                },
                label = { Text("Número de canvis") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            TextField(
                value = skinCondition,
                onValueChange = onSkinConditionChange,
                label = { Text("Estat de la pell") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }
}


@Composable
fun ItemInfo(
    label: String,
    info: String,
    onInfoChange: (String) -> Unit,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
) {
    val infoFontSize = 18.sp
    val labelFontSize = 20.sp

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
                text = label, style = MaterialTheme.typography.titleMedium
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
                label = { Text("Escriu aquí") },
                modifier = Modifier.fillMaxWidth(),

                )
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun PreviewDiagnosis() {
    val navController = rememberNavController()
    val diagnosisViewModel = DiagnosisViewModel()
    val diagnosisRemoteViewModel = DiagnosisRemoteViewModel()
    val id = 1
    HospitalFrontEndTheme {
        DiagnosisScreen(navController, diagnosisRemoteViewModel, diagnosisViewModel, id)
    }
}
 */