package com.example.hospitalfrontend.ui.patients.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.*
import com.example.hospitalfrontend.data.remote.viewmodel.*
import com.example.hospitalfrontend.domain.model.facility.RoomDTO
import com.example.hospitalfrontend.domain.model.facility.RoomState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.diagnosis.view.NoDataInformation
import com.example.hospitalfrontend.ui.diagnosis.view.NunitoFontFamily
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientSharedViewModel
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignExistingPatientView(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    roomId: String,
    sharedViewModel: PatientSharedViewModel
) {
    val idsAsignados by sharedViewModel.idsAsignados.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<PatientState?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var assignedPatientName by remember { mutableStateOf("") }
    val customPrimaryColor = Color(0xFFA9C7C7)
    val patients by patientViewModel.patients.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    val remoteApiMessageListPatient = patientRemoteViewModel.remoteApiListMessagePatient.value
    val remoteApiMessageUpdatePatient = patientRemoteViewModel.remoteApiUpdatePatient.value

    LaunchedEffect(Unit) {
        patientRemoteViewModel.getAllPatients()
        val currentRooms = patientViewModel.rooms.value
        sharedViewModel.updateIdsFromRooms(currentRooms)
    }

    val filteredPatients = remember(patients, searchText, idsAsignados) {
        val filteredBySearch = if (searchText.isEmpty()) {
            patients
        } else {
            val terms = searchText.trim().split(" ")
            patients.filter { patient ->
                terms.all { term ->
                    patient.name.contains(term, ignoreCase = true) || patient.surname.contains(
                        term,
                        ignoreCase = true
                    )
                }
            }
        }

        val finalFiltered = filteredBySearch.filter { patient ->
            val shouldInclude = patient.historialNumber !in idsAsignados
            println("🔍 Paciente ${patient.name} (ID: ${patient.historialNumber}) - ¿Incluir?: $shouldInclude")
            shouldInclude
        }

        println("🔍 Pacientes después del filtro: ${finalFiltered.size}")
        finalFiltered
    }

    LaunchedEffect(Unit) {
        patientRemoteViewModel.getAllPatients()
    }

    LaunchedEffect(remoteApiMessageListPatient) {
        when (remoteApiMessageListPatient) {
            is RemoteApiMessageListPatient.Success -> {
                patientViewModel.loadPatient(remoteApiMessageListPatient.message)
                isLoading = false
            }

            is RemoteApiMessageListPatient.Loading -> {
                isLoading = true
            }

            is RemoteApiMessageListPatient.Error -> {
                isLoading = false
            }
        }
    }

    LaunchedEffect(remoteApiMessageUpdatePatient) {
        when (remoteApiMessageUpdatePatient) {
            is RemoteApiMessagePatientUpdate.Success -> {
                showSuccessDialog = true
                showConfirmDialog = false
                patientRemoteViewModel.clearApiMessage()
                patientRemoteViewModel.getAllRooms()
            }

            is RemoteApiMessagePatientUpdate.Error -> {
                showErrorDialog = true
                showConfirmDialog = false
            }
        }
    }

    Scaffold(
        containerColor = customPrimaryColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.assign_search_title),
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            Icons.Filled.Close, contentDescription = "Tornar", tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customPrimaryColor,
                    scrolledContainerColor = customPrimaryColor
                ),
            )
        }
    ) { paddingValues ->
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
                        color = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else {
                if (patients.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(customPrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        NoDataInformation(
                            labelRes = R.string.assign_search_emptyPatients,
                            infoRes = R.string.assign_search_emptyPatientsCreate,
                            icon = Icons.Filled.ContactMail
                        )

                        FloatingActionButton(
                            onClick = {
                                navController.navigate("createPatient")
                            },
                            containerColor = Color(0xFF4CAF50),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(24.dp),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Patient", tint = Color.White)
                        }
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

                        if (filteredPatients.isEmpty() && searchText.isNotEmpty()) {
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
                                        text = stringResource(id = R.string.assign_search_notFoundTitle),
                                        style = TextStyle(
                                            fontFamily = NunitoFontFamily,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = stringResource(id = R.string.assign_search_notFoundText),
                                        style = TextStyle(
                                            fontFamily = LatoFontFamily,
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        } else {
                            filteredPatients.forEach { patient ->
                                PatientDetailCard(
                                    patient, navController, onPatientSelected = { selected ->
                                        selectedPatient = selected
                                        showConfirmDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog && selectedPatient != null) {
        AlertDialog(onDismissRequest = {
            showConfirmDialog = false
            selectedPatient = null
        }, icon = {
            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = "Assignar pacient",
                tint = Color(0xFFA9C7C7)
            )
        }, title = {
            Text(
                text = stringResource(id = R.string.assign_search_dialogTitle ), style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = stringResource(id = R.string.assign_search_dialogText,
                    selectedPatient?.name ?: "",
                    selectedPatient?.surname ?: ""
                    ),
                style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = 16.sp
                )
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    selectedPatient?.let { patient ->
                        assignedPatientName = "${patient.name} ${patient.surname ?: ""}".trim()
                        val roomDto = RoomDTO(
                            room = RoomState(roomId = roomId),
                            patient = PatientState(historialNumber = patient.historialNumber)
                        )
                        patientRemoteViewModel.updatePatientAssign(roomDto)
                    }
                }) {
                Text(
                    stringResource(id = R.string.assign_search_DialogOk), style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA9C7C7)
                    )
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    showConfirmDialog = false
                    selectedPatient = null
                }) {
                Text(
                    stringResource(id = R.string.assign_search_DialogCancel), style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                )
            }
        })
    }

    if (showSuccessDialog) {
        AlertDialog(onDismissRequest = {
            showSuccessDialog = false
            selectedPatient = null
            navController.popBackStack()
        }, icon = {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Èxit",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
        }, title = {
            Text(
                text = "Assignació exitosa", style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = "El pacient $assignedPatientName ha estat assignat correctament a l'habitació.",
                style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = 16.sp
                )
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    showSuccessDialog = false
                    selectedPatient = null
                    navController.navigate("home")
                }) {
                Text(
                    "D'acord", style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                )
            }
        })
    }

    if (showErrorDialog) {
        AlertDialog(onDismissRequest = {
            showErrorDialog = false
            selectedPatient = null
        }, icon = {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Error",
                tint = Color(0xFFF44336),
                modifier = Modifier.size(48.dp)
            )
        }, title = {
            Text(
                text = "Error en l'assignació", style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }, text = {
            Text(
                text = "No s'ha pogut assignar el pacient a l'habitació. Si us plau, intenta-ho de nou.",
                style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = 16.sp
                )
            )
        }, confirmButton = {
            TextButton(
                onClick = {
                    showErrorDialog = false
                    selectedPatient = null
                }) {
                Text(
                    "D'acord", style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
                    )
                )
            }
        })
    }
}

@Composable
fun PatientDetailCard(
    patient: PatientState, navController: NavController, onPatientSelected: (PatientState) -> Unit
) {
    val customIconColor = Color(0xFF505050)
    val defaultInfoColor = Color(0xFF7F8C8D)

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
                    text = "${patient.name} ${patient.surname}",
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                TextButton(
                    onClick = {
                        onPatientSelected(patient)
                    }, colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.assign_search_choose),
                            color = customIconColor,
                            style = TextStyle(
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Actualizar",
                            tint = customIconColor,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
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
                text = stringResource(id = R.string.assign_search_placeholder), style = TextStyle(
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