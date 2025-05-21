package com.example.hospitalfrontend.ui.medication.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCreateMedication
import com.example.hospitalfrontend.data.remote.viewmodel.MedicationRemoteViewModel
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.medication.viewmodel.MedicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMedicationScreen(
    navController: NavHostController,
    medicationViewModel: MedicationViewModel,
    medicationRemoteViewModel: MedicationRemoteViewModel,
    isError: MutableState<Boolean>,
) {

    val stockMedication = rememberSaveable { mutableStateOf("") }
    val nameMedication = rememberSaveable { mutableStateOf("") }
    val dosageMedication = rememberSaveable { mutableStateOf("") }
    val adminstrationRouteMedication = rememberSaveable { mutableStateOf("") }

    val customPrimaryColor = Color(0xFFA9C7C7)

    var isLoading by remember { mutableStateOf(false) }
    val createMedicationError = remember { mutableStateOf(false) }
    val latoLightFont = FontFamily(Font(R.font.lato_light))
    val messageApi = medicationRemoteViewModel.remoteCreateMedication.value

    val isFormValidState = remember { mutableStateOf(false) }

    LaunchedEffect(
        nameMedication.value,
        dosageMedication.value,
        adminstrationRouteMedication.value,
        stockMedication.value,

        ) {
        isFormValidState.value = isFormValid(
            nameMedication.value,
            dosageMedication.value,
            adminstrationRouteMedication.value,
            stockMedication.value,
        )
    }

    LaunchedEffect(Unit) {
        createMedicationError.value = false
        isError.value = false
        medicationRemoteViewModel.clearApiMessage()
    }

    LaunchedEffect(messageApi) {
        when (messageApi) {
            is RemoteApiMessageCreateMedication.Success -> {
                medicationRemoteViewModel.clearApiMessage()
                isLoading = false
                navController.popBackStack()
            }

            RemoteApiMessageCreateMedication.Loading -> {
                isLoading = true
            }

            RemoteApiMessageCreateMedication.Error -> {
                createMedicationError.value = true
                isLoading = false
            }

            RemoteApiMessageCreateMedication.Idle -> {
                isLoading = false
            }

        }
    }
    DisposableEffect(Unit) {
        onDispose {
            medicationRemoteViewModel.clearApiMessage()
        }
    }
    if (createMedicationError.value) {
        AlertDialog(onDismissRequest = {
            createMedicationError.value = false
            medicationRemoteViewModel.clearApiMessage()
        }, confirmButton = {
            TextButton(onClick = {
                createMedicationError.value = false
                medicationRemoteViewModel.clearApiMessage()
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
                            text = "CREAR MEDICINA",
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
                        medicationRemoteViewModel.clearApiMessage()
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
                CreateMedicationDetailsCard(
                    nameInfo = nameMedication,
                    stockInfo = stockMedication,
                    dosageInfo = dosageMedication,
                    adminstrationRouteInfo = adminstrationRouteMedication
                )
                Button(
                    onClick = {
                        val stockValue = stockMedication.value.toIntOrNull() ?: 0
                        val medicationState = MedicationState(
                            id = 0,
                            name = nameMedication.value,
                            dosage = dosageMedication.value,
                            adminstrationRoute = adminstrationRouteMedication.value,
                            stock = stockValue,
                        )

                        isError.value = false
                        medicationRemoteViewModel.addMedicine(medicationState)
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
fun CreateMedicationDetailsCard(
    stockInfo: MutableState<String>,
    nameInfo: MutableState<String>,
    dosageInfo: MutableState<String>,
    adminstrationRouteInfo: MutableState<String>

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
            ItemInfo(
                labelResId = R.string.medication_name,
                info = nameInfo.value,
                onInfoChange = { nameInfo.value = it },
                icon = Icons.Filled.Water,
                iconColor = customIconColor
            )
            ItemInfo(
                labelResId = R.string.medication_dossage,
                info = dosageInfo.value,
                onInfoChange = { dosageInfo.value = it },
                icon = Icons.Filled.Medication,
                iconColor = customIconColor
            )
            ItemInfo(
                labelResId = R.string.medication_administration,
                info = adminstrationRouteInfo.value,
                onInfoChange = { adminstrationRouteInfo.value = it },
                icon = Icons.Filled.HealthAndSafety,
                iconColor = customIconColor
            )
            ItemInfo(
                labelResId = R.string.medication_stock,
                info = stockInfo.value,
                onInfoChange = { stockInfo.value = it },
                icon = Icons.Filled.HealthAndSafety,
                iconColor = customIconColor
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
    nameInfo: String,
    dosageInfo: String,
    adminstrationRouteInfo: String,
    stockInfo: String,
): Boolean {
    return nameInfo.isNotEmpty() &&
            dosageInfo.isNotEmpty() &&
            adminstrationRouteInfo.isNotEmpty() &&
            stockInfo.toIntOrNull() != null
}