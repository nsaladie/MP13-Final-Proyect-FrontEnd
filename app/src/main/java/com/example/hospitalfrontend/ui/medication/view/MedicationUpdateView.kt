package com.example.hospitalfrontend.ui.medication.view

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.*
import com.example.hospitalfrontend.data.remote.viewmodel.MedicationRemoteViewModel
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AppColors {
    val Primary = Color(0xFFA9C7C7)
    val Secondary = Color(0xFF2ECC71)
    val TextPrimary = Color(0xFF2C3E50)
    val TextSecondary = Color(0xFFC4C4C4)
    val BorderUnfocused = Color(0xFFBDC3C7)
    val White = Color.White
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateMedicationScreen(
    navController: NavController,
    medicationRemoteViewModel: MedicationRemoteViewModel,
    medicationId: Int
) {
    val scope = rememberCoroutineScope()

    // UI State
    var isLoading by remember { mutableStateOf(true) }
    var medicationState by remember { mutableStateOf<MedicationState?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val dialogText = listOf(
        stringResource(R.string.dialog_update_medication_text_success),
        stringResource(
            R.string.dialog_update_medication_text_fail
        )
    )

    // Form State
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var administrationRoute by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val administrationOptions = listOf(
        stringResource(id = R.string.medication_route_oral),
        stringResource(id = R.string.medication_route_injectable),
        stringResource(id = R.string.medication_route_topical),
        stringResource(id = R.string.medication_route_inhalation),
        stringResource(id = R.string.medication_route_sublingual)
    )

    val remoteMedication = medicationRemoteViewModel.remoteMedication.value
    val updateMedicationResponse = medicationRemoteViewModel.remoteUpdateMedication.value

    // Load data from medicine
    LaunchedEffect(medicationId) {
        medicationRemoteViewModel.getMedicationId(medicationId)
    }

    LaunchedEffect(remoteMedication) {
        when (remoteMedication) {
            is RemoteApiMessageMedication.Success -> {
                val medication = remoteMedication.message
                medicationState = medication
                name = medication.name
                dosage = medication.dosage
                administrationRoute = medication.adminstrationRoute
                stock = medication.stock.toString()
                isLoading = false
            }

            is RemoteApiMessageMedication.Loading -> {
                isLoading = true
            }

            is RemoteApiMessageMedication.Error -> {
                isLoading = false
            }
        }
    }

    // Update Data
    LaunchedEffect(updateMedicationResponse) {
        when (updateMedicationResponse) {
            is RemoteApiMessageBoolean.Success -> {
                showSuccessDialog = true
                medicationRemoteViewModel.resetMessage()
            }

            is RemoteApiMessageBoolean.Loading -> {
                isLoading = true
            }

            is RemoteApiMessageBoolean.Error -> {
                isLoading = false
                showErrorDialog = true
                medicationRemoteViewModel.resetMessage()
            }
        }
    }

    // Show Dialog
    if (showSuccessDialog) {
        StatusDialog(
            title = stringResource(R.string.dialog_update_medication_title_success),
            message = dialogText[0],
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

    DisposableEffect(Unit) {
        onDispose {
            medicationRemoteViewModel.resetMessage()
        }
    }

    if (showErrorDialog) {
        StatusDialog(
            title = stringResource(R.string.dialog_update_medication_title_fail),
            message = dialogText[1],
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
                title = stringResource(id = R.string.medication_update_title),
                onNavigateBack = { navController.popBackStack() }
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
                    MedicationForm(
                        name = name,
                        onNameChange = { name = it },
                        dosage = dosage,
                        onDosageChange = { dosage = it },
                        administrationRoute = administrationRoute,
                        onRouteChange = { administrationRoute = it },
                        stock = stock,
                        onStockChange = {
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) stock = it
                        },
                        administrationOptions = administrationOptions,
                        dropdownExpanded = dropdownExpanded,
                        onDropdownExpandedChange = { dropdownExpanded = it },
                        onUpdateClick = {
                            val updatedMedication = medicationState?.copy(
                                name = name,
                                dosage = dosage,
                                adminstrationRoute = administrationRoute,
                                stock = stock.toIntOrNull() ?: 0,
                            )
                            updatedMedication?.let {
                                medicationRemoteViewModel.updateMedication(medicationId, it)
                            }
                        },
                        isFormValid = name.isNotBlank() && dosage.isNotBlank() &&
                                administrationRoute.isNotBlank() && stock.isNotBlank()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationTopBar(
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
                    contentDescription = "Return",
                    tint = AppColors.TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.Primary,
            scrolledContainerColor = AppColors.Primary
        )
    )
}

@Composable
fun MedicationForm(
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
    onUpdateClick: () -> Unit,
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

            // Update button
            Button(
                onClick = onUpdateClick,
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
                        text = stringResource(id = R.string.update_button),
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

@Composable
fun MedicationFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    keyboardType: KeyboardType
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = AppColors.Primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.BorderUnfocused,
                focusedContainerColor = AppColors.White,
                unfocusedContainerColor = AppColors.White
            ),
            textStyle = TextStyle(
                fontFamily = LatoFontFamily,
                fontSize = 18.sp,
                color = AppColors.TextPrimary
            ),
            placeholder = {
                Text(
                    placeholder,
                    color = AppColors.TextSecondary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdministrationRouteSelector(
    selectedRoute: String,
    options: List<String>,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.medication_administration_route),
            style = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        )

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedRoute,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                leadingIcon = {
                    Icon(
                        getAdministrationRouteIcon(selectedRoute),
                        contentDescription = null,
                        tint = AppColors.Primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.BorderUnfocused,
                    focusedContainerColor = AppColors.White,
                    unfocusedContainerColor = AppColors.White
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = LatoFontFamily,
                    fontSize = 18.sp,
                    color = AppColors.TextPrimary
                ),
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .background(AppColors.White)
                    .exposedDropdownSize()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    getAdministrationRouteIcon(option),
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = option,
                                    style = TextStyle(
                                        fontFamily = LatoFontFamily,
                                        fontSize = 16.sp,
                                        color = AppColors.TextPrimary
                                    )
                                )
                            }
                        },
                        onClick = {
                            onOptionSelected(option)
                            onExpandedChange(false)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = AppColors.TextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StatusDialog(
    title: String,
    message: String,
    icon: ImageVector,
    iconTint: Color,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.White),
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
                        color = AppColors.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 18.sp,
                        color = AppColors.TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
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