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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.*
import com.example.hospitalfrontend.data.remote.viewmodel.*
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NoDataInformation
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.medication.viewmodel.MedicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    navController: NavController,
    medicationViewModel: MedicationViewModel,
    medicationRemoteViewModel: MedicationRemoteViewModel
) {
    val customPrimaryColor = Color(0xFFA9C7C7)
    var isLoading by remember { mutableStateOf(true) }
    val listMedication by medicationViewModel.listMedication.collectAsState()
    val remoteListMedication = medicationRemoteViewModel.remoteListMedication.value
    var searchText by remember { mutableStateOf("") }

    val filteredMedications = remember(listMedication, searchText) {
        if (searchText.isEmpty()) {
            listMedication
        } else {
            listMedication.filter {
                it.name.contains(searchText, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        medicationRemoteViewModel.getAllMedication()
    }

    LaunchedEffect(remoteListMedication) {
        when (remoteListMedication) {
            is RemoteApiMessageListMedication.Success -> {
                medicationViewModel.loadListMedication(remoteListMedication.message)
                isLoading = false
            }

            is RemoteApiMessageListMedication.Loading -> {
                isLoading = true
            }

            is RemoteApiMessageListMedication.Error -> {
                isLoading = false
            }
        }
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
                            text = "MEDICAMENTS", style = TextStyle(
                                fontSize = 30.sp,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customPrimaryColor, scrolledContainerColor = customPrimaryColor
                ),
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor),
            contentAlignment = Alignment.Center
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

            if (!isLoading && listMedication.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(customPrimaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    NoDataInformation(
                        labelRes = R.string.empty_medication,
                        infoRes = R.string.create_diagnosis,
                        icon = Icons.Filled.Medication
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    // Search Field
                    SearchField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    // Message when there isn't any result of search
                    if (filteredMedications.isEmpty() && searchText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SearchOff,
                                    contentDescription = "No se encontraron resultados",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No s'han trobat medicaments", style = TextStyle(
                                        fontFamily = NunitoFontFamily,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Intenta amb un altre nom", style = TextStyle(
                                        fontFamily = LatoFontFamily,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        // Show all the list filtered of medicines
                        filteredMedications.forEach { medication ->
                            MedicationDetailCard(medication, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationDetailCard(medication: MedicationState, navController: NavController) {
    val customIconColor = Color(0xFF505050)
    val defaultInfoColor = Color(0xFF7F8C8D)
    val stockColor = when {
        medication.stock <= 10 -> Color(0xFFE74C3C) // Red for low stock
        medication.stock <= 25 -> Color(0xFFFF9800) // Orange for medium stock
        else -> Color(0xFF2ECC71) // Green for high stock
    }

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
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.name,
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                TextButton(
                    onClick = {
                        navController.navigate("medicationDetail/${medication.id}")
                    }, colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Actualizar", color = customIconColor, style = TextStyle(
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Actualizar",
                            tint = customIconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            DetailItemWithIcon(
                label = "Dosi",
                info = medication.dosage,
                icon = Icons.Filled.Science,
                iconColor = customIconColor,
                infoColor = defaultInfoColor
            )

            DetailItemWithIcon(
                label = "Via d'administració",
                info = medication.adminstrationRoute,
                icon = getAdministrationRouteIcon(medication.adminstrationRoute),
                iconColor = customIconColor,
                infoColor = defaultInfoColor
            )

            DetailItemWithIcon(
                label = "Stock disponible",
                info = "${medication.stock} unitats",
                icon = Icons.Filled.Inventory,
                iconColor = stockColor,
                infoColor = stockColor
            )
        }
    }
}

@Composable
fun DetailItemWithIcon(
    label: String,
    info: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    infoColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label, style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )
            Text(
                text = info, style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = 18.sp, color = infoColor
                )
            )
        }
    }
}

@Composable
fun SearchField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Cerca medicaments...", style = TextStyle(
                    fontFamily = LatoFontFamily, color = Color(0xFF7F8C8D)
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Icona de cerca",
                tint = Color(0xFF505050)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Esborrar cerca",
                        tint = Color(0xFF505050)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Color(0xFF505050),
            focusedTextColor = Color(0xFF2C3E50),
            unfocusedTextColor = Color(0xFF2C3E50)
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

fun getAdministrationRouteIcon(route: String): ImageVector {
    return when (route.lowercase()) {
        "oral" -> Icons.Filled.LocalDrink
        "inyectable", "intravenosa", "intravenoso" -> Icons.Filled.Vaccines
        "tópica", "tópico" -> Icons.Filled.Healing
        "inhalación", "inhalada" -> Icons.Filled.Air
        "sublingual" -> Icons.Filled.Spa
        else -> Icons.Filled.Medication
    }
}