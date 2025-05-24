package com.example.hospitalfrontend.ui.medication.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageCreateMedication
import com.example.hospitalfrontend.data.remote.viewmodel.MedicationRemoteViewModel
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CreateMedicationScreen(
    navController: NavHostController,
    medicationRemoteViewModel: MedicationRemoteViewModel,
) {
    val isError = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Form State
    val stockMedication = rememberSaveable { mutableStateOf("") }
    val nameMedication = rememberSaveable { mutableStateOf("") }
    val dosageMedication = rememberSaveable { mutableStateOf("") }
    val adminstrationRouteMedication = rememberSaveable { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // UI State
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val administrationOptions = listOf(
        stringResource(id = R.string.medication_route_oral),
        stringResource(id = R.string.medication_route_injectable),
        stringResource(id = R.string.medication_route_topical),
        stringResource(id = R.string.medication_route_inhalation),
        stringResource(id = R.string.medication_route_sublingual)
    )

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
        isError.value = false
        medicationRemoteViewModel.clearApiMessage()
    }

    LaunchedEffect(messageApi) {
        when (messageApi) {
            is RemoteApiMessageCreateMedication.Success -> {
                showSuccessDialog = true
                medicationRemoteViewModel.clearApiMessage()
            }

            RemoteApiMessageCreateMedication.Loading -> {
                isLoading = true
            }

            RemoteApiMessageCreateMedication.Error -> {
                isLoading = false
                showErrorDialog = true
                medicationRemoteViewModel.clearApiMessage()
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

    // Success Dialog
    if (showSuccessDialog) {
        StatusDialog(
            title = stringResource(R.string.dialog_create_medication_title_success),
            message = stringResource(R.string.dialog_create_medication_text_success),
            icon = Icons.Filled.CheckCircle,
            iconTint = AppColors.Secondary,
            onDismiss = {
                showSuccessDialog = false
                scope.launch {
                    delay(300)
                    navController.popBackStack()
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        StatusDialog(
            title = stringResource(R.string.error_title),
            message = stringResource(R.string.dialog_create_medication_text_fail),
            icon = Icons.Filled.Error,
            iconTint = Color.Red,
            onDismiss = {
                showErrorDialog = false
            }
        )
    }

    Scaffold(
        containerColor = AppColors.Primary,
        topBar = {
            MedicationTopBar(
                title = stringResource(id = R.string.create_medication_title),
                onNavigateBack = {
                    medicationRemoteViewModel.clearApiMessage()
                    isError.value = false
                    navController.popBackStack()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Primary)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = AppColors.White,
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
                    CreateMedicationForm(
                        name = nameMedication.value,
                        onNameChange = { nameMedication.value = it },
                        dosage = dosageMedication.value,
                        onDosageChange = { dosageMedication.value = it },
                        administrationRoute = adminstrationRouteMedication.value,
                        onRouteChange = { adminstrationRouteMedication.value = it },
                        stock = stockMedication.value,
                        onStockChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { char -> char.isDigit() }) {
                                stockMedication.value = newValue
                            }
                        },
                        administrationOptions = administrationOptions,
                        dropdownExpanded = dropdownExpanded,
                        onDropdownExpandedChange = { dropdownExpanded = it },
                        onCreateClick = {
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
                        isFormValid = isFormValidState.value
                    )
                }
            }
        }
    }
}

@Composable
fun CreateMedicationForm(
    name: String,
    onNameChange: (String) -> Unit,
    dosage: String,
    onDosageChange: (String) -> Unit,
    administrationRoute: String,
    onRouteChange: (String) -> Unit,
    stock: String,
    onStockChange: (String) -> Unit,
    administrationOptions: List<String>,
    dropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    onCreateClick: () -> Unit,
    isFormValid: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            MedicationFormField(
                value = name,
                onValueChange = onNameChange,
                label = stringResource(id = R.string.medication_name),
                icon = Icons.Filled.Medication,
                placeholder = stringResource(id = R.string.placeholder_medication_name),
                keyboardType = KeyboardType.Text
            )

            MedicationFormField(
                value = dosage,
                onValueChange = onDosageChange,
                label = stringResource(id = R.string.medication_dosage),
                icon = Icons.Filled.Science,
                placeholder = stringResource(id = R.string.placeholder_medication_dosage),
                keyboardType = KeyboardType.Text
            )

            AdministrationRouteSelector(
                selectedRoute = administrationRoute,
                options = administrationOptions,
                isExpanded = dropdownExpanded,
                onExpandedChange = onDropdownExpandedChange,
                onOptionSelected = onRouteChange
            )

            MedicationFormField(
                value = stock,
                onValueChange = onStockChange,
                label = stringResource(id = R.string.medication_stock),
                placeholder = stringResource(id = R.string.placeholder_medication_stock),
                icon = Icons.Filled.Inventory,
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Create button
            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Secondary,
                    disabledContainerColor = AppColors.Secondary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Save,
                        contentDescription = null,
                        tint = AppColors.White
                    )
                    Text(
                        text = stringResource(id = R.string.button_save),
                        style = TextStyle(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppColors.White
                        )
                    )
                }
            }
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