package com.example.hospitalfrontend.ui.patients.view

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessagePatient
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.domain.model.facility.RoomDTO
import com.example.hospitalfrontend.domain.model.facility.RoomState
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePatientData(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    patientId: Int,
    roomId: String,
) {
    var patientState by remember { mutableStateOf<PatientState?>(null) }
    var updateRequested by remember { mutableStateOf(false) }

    val nunitoFont = FontFamily(Font(R.font.nunito_bold))
    val latoFont = FontFamily(Font(R.font.lato_regular))

    val backgroundColor = Color(169, 199, 199)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Form data states
    val nameValue =
        rememberSaveable { mutableStateOf(patientState?.name ?: "") }
    val surnameValue =
        rememberSaveable { mutableStateOf(patientState?.surname ?: "") }
    val addressValue =
        rememberSaveable { mutableStateOf(patientState?.direction ?: "") }
    val birthdayValue = rememberSaveable {
        mutableStateOf(patientState?.dateBirth?.let {
            dateFormat.format(it)
        } ?: "")
    }
    val languageValue =
        rememberSaveable { mutableStateOf(patientState?.language ?: "") }
    val antecedentsMedics =
        rememberSaveable { mutableStateOf(patientState?.history ?: "") }
    val caregiverName = rememberSaveable {
        mutableStateOf(
            patientState?.caragiverName ?: ""
        )
    }
    val caregiverNumber = rememberSaveable {
        mutableStateOf(
            patientState?.caragiverNumber ?: ""
        )
    }
    val allergiesValue =
        rememberSaveable { mutableStateOf(patientState?.allergy ?: "") }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }
    val remoteApiMessage = patientRemoteViewModel.remoteApiMessage.value
    val isFormValid by remember {
        derivedStateOf {
            nameValue.value.isNotBlank() &&
                    surnameValue.value.isNotBlank() &&
                    addressValue.value.isNotBlank() &&
                    birthdayValue.value.isNotBlank() &&
                    languageValue.value.isNotBlank() &&
                    birthdayValue.value.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.assign_create_title),
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = nunitoFont,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        ),
                        color = colorResource(id = colorText),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back",
                            tint = Color.DarkGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(60.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Main content card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Form content
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {

                            // Scrollable Form
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                // Patient Information Section
                                SectionHeader(
                                    text = stringResource(id = R.string.patient_info),
                                    latoFont
                                )

                                EnhancedTextField(
                                    labelRes = R.string.name,
                                    icon = Icons.Default.Person,
                                    textValue = nameValue,
                                    fontFamily = latoFont,
                                    readOnly = false
                                )

                                EnhancedTextField(
                                    labelRes = R.string.surname,
                                    icon = Icons.Default.Person,
                                    textValue = surnameValue,
                                    fontFamily = latoFont,
                                    readOnly = false
                                )

                                EnhancedBirthdayField(
                                    labelRes = R.string.birthday,
                                    icon = Icons.Default.Today,
                                    dateValue = birthdayValue,
                                    fontFamily = latoFont,
                                    readOnly = false
                                )

                                EnhancedTextField(
                                    labelRes = R.string.address,
                                    icon = Icons.Default.LocationOn,
                                    textValue = addressValue,
                                    fontFamily = latoFont,
                                    readOnly = false
                                )

                                EnhancedTextField(
                                    labelRes = R.string.language,
                                    icon = Icons.Default.Language,
                                    textValue = languageValue,
                                    fontFamily = latoFont,
                                    readOnly = false
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Save Button
                            EnhancedSaveButtonWithHelper(
                                text = stringResource(id = R.string.dades_button),
                                isEnabled = isFormValid,
                                fontFamily = latoFont,
                                helperText = stringResource(id = R.string.button_save_create)
                            ) {

                                if (isFormValid) {
                                    val parsedDate = try {
                                        dateFormat.parse(birthdayValue.value)
                                    } catch (e: ParseException) {
                                        null
                                    }
                                    // Action to save data
                                    val createPatientData = PatientState(
                                        name = nameValue.value,
                                        surname = surnameValue.value,
                                        direction = addressValue.value,
                                        dateBirth = parsedDate,
                                        language = languageValue.value
                                    )
                                    patientRemoteViewModel.createPatient(createPatientData)
                                    updateRequested = true
                                }
                            }
                        }

                        val successMessage =
                            stringResource(id = R.string.assign_create_successAlert)
                        val errorMessage =
                            stringResource(id = R.string.assign_create_successError)
                        LaunchedEffect(remoteApiMessage, updateRequested) {
                            if (updateRequested) {
                                when (remoteApiMessage) {
                                    is RemoteApiMessagePatient.Success -> {
                                        /*
                                        Log.d("PatientSuccess", "Success message: ${remoteApiMessage.message}")
                                        val createdPatient = remoteApiMessage.message

                                        val roomDTO = RoomDTO(
                                            room = RoomState(roomId = roomId),
                                            patient = PatientState(historialNumber = createdPatient.historialNumber)
                                        )
                                        patientRemoteViewModel.updatePatientAssign(roomDTO)
                                        */
                                        dialogMessage = successMessage
                                        showSuccessDialog = true
                                        updateRequested = false
                                    }

                                    is RemoteApiMessagePatient.Error -> {
                                        dialogMessage = errorMessage
                                        showErrorDialog = true
                                        updateRequested = false
                                    }

                                    RemoteApiMessagePatient.Loading -> Log.d(
                                        "Loading Update",
                                        "Loading"
                                    )
                                }
                            }
                        }
                        SuccessDialog(
                            showDialog = showSuccessDialog,
                            message = dialogMessage,
                            onDismiss = { showSuccessDialog = false },
                            navController = navController
                        )
                        ErrorDialog(
                            showDialog = showErrorDialog,
                            message = dialogMessage,
                            onDismiss = { showErrorDialog = false },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    navController: NavController
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(id = R.string.assign_create_success)) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss
                        navController.navigate("home")
                    }
                ) {
                    Text(stringResource(id = R.string.dialog_ok))
                }
            }
        )
    }
}

@Composable
fun ErrorDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    navController: NavController
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(id = R.string.error_title)) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.dialog_ok))
                }
            }
        )
    }
}

@Composable
fun SectionHeader(text: String, fontFamily: FontFamily) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            ),
            color = Color(80, 80, 80),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

@Composable
fun EnhancedNumberField(
    labelValue: String,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily,
    maxLength: Int = Int.MAX_VALUE
) {
    val isError = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = textValue.value,
            onValueChange = {
                if (it.matches(Regex("^\\d{0,9}$"))) { // Permite hasta 9 dígitos
                    textValue.value = it
                    isError.value = false
                } else {
                    isError.value = true
                }
            },
            label = {
                Text(
                    text = labelValue,
                    fontFamily = fontFamily,
                    fontSize = 14.sp
                )
            },
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            },
            isError = isError.value,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(250, 250, 250),
                unfocusedContainerColor = Color(250, 250, 250),
                focusedIndicatorColor = Color(151, 199, 150),
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color(151, 199, 150),
                focusedLabelColor = Color(151, 199, 150),
                errorIndicatorColor = Color.Red,
                errorLabelColor = Color.Red
            ),
            textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 18.sp,
                color = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (isError.value && textValue.value.isNotEmpty()) {
            Text(
                text = "Només es permet 9 dígits",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }

    }
}

@Composable
fun EnhancedTextField(
    @StringRes labelRes: Int,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily,
    readOnly: Boolean = false
) {
    val labelValue = stringResource(id = labelRes)
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        readOnly = readOnly,
        label = {
            Text(
                text = labelValue,
                fontFamily = fontFamily,
                fontSize = 14.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(250, 250, 250),
            unfocusedContainerColor = Color(250, 250, 250),
            focusedIndicatorColor = Color(151, 199, 150),
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = Color(151, 199, 150),
            focusedLabelColor = Color(151, 199, 150),
        ),
        textStyle = TextStyle(
            fontFamily = fontFamily,
            fontSize = 18.sp,
            color = Color.DarkGray
        )
    )
}

@Composable
fun EnhancedMultilineField(
    @StringRes labelRes: Int,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily,
    readOnly: Boolean = false
) {
    val labelValue = stringResource(id = labelRes)
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        readOnly = readOnly,
        label = {
            Text(
                text = labelValue,
                fontFamily = fontFamily,
                fontSize = 14.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(250, 250, 250),
            unfocusedContainerColor = Color(250, 250, 250),
            focusedIndicatorColor = Color(151, 199, 150),
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = Color(151, 199, 150),
            focusedLabelColor = Color(151, 199, 150),
        ),
        textStyle = TextStyle(
            fontFamily = fontFamily,
            fontSize = 18.sp,
            color = Color.DarkGray
        ),
        minLines = 3
    )
}

@Composable
fun EnhancedBirthdayField(
    @StringRes labelRes: Int,
    icon: ImageVector,
    dateValue: MutableState<String>,
    fontFamily: FontFamily,
    readOnly: Boolean = false
) {
    val labelValue = stringResource(id = labelRes)
    OutlinedTextField(
        value = dateValue.value,
        onValueChange = { dateValue.value = it },
        readOnly = readOnly,
        label = {
            Text(
                text = labelValue,
                fontFamily = fontFamily,
                fontSize = 14.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(250, 250, 250),
            unfocusedContainerColor = Color(250, 250, 250),
            focusedIndicatorColor = Color(151, 199, 150),
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = Color(151, 199, 150),
            focusedLabelColor = Color(151, 199, 150),
        ),
        textStyle = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            color = Color.DarkGray
        ),
        placeholder = {
            Text(
                text = "dd/mm/yyyy",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    )
}

@Composable
fun EnhancedSaveButtonWithHelper(
    text: String,
    isEnabled: Boolean,
    fontFamily: FontFamily,
    helperText: String = "",
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp)
                .padding(vertical = 8.dp),
            enabled = isEnabled,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(151, 199, 150),
                disabledContainerColor = Color.LightGray
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
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
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = Color.White
                )
            }
        }

        AnimatedVisibility(visible = !isEnabled && helperText.isNotEmpty()) {
            Text(
                text = helperText,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}