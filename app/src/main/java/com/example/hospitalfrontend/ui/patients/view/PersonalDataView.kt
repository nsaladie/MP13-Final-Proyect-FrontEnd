import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessagePatient
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.data.remote.viewmodel.PatientRemoteViewModel
import com.example.hospitalfrontend.ui.cure.view.EnhancedSaveButton
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalData(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    patientId: Int,
) {
    var patientState by remember { mutableStateOf<PatientState?>(null) }
    var updateRequested by remember { mutableStateOf(false) }
    var dataLoaded by remember { mutableStateOf(false) }
    var patientData by remember { mutableStateOf<PatientState?>(null) }
    LaunchedEffect(patientId) {
        patientRemoteViewModel.getPatientById(patientId, patientViewModel)
    }

    LaunchedEffect(patientViewModel.patientState) {

        patientViewModel.patientState.collect { newState ->
            patientState = newState
        }
    }
    LaunchedEffect(Unit) {
        patientViewModel.patientState.collect { newState ->
            patientData = newState
            if (newState != null) {
                dataLoaded = true
            }
        }
    }
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
    // Actualizar los valores cuando los datos se cargan
    LaunchedEffect(patientData, dataLoaded) {
        if (dataLoaded && patientData != null) {
            nameValue.value = patientData?.name ?: ""
            surnameValue.value = patientData?.surname ?: ""
            addressValue.value = patientData?.direction ?: ""
            birthdayValue.value = patientData?.dateBirth?.let { dateFormat.format(it) } ?: ""
            languageValue.value = patientData?.language ?: ""
            antecedentsMedics.value = patientData?.history ?: ""
            caregiverName.value = patientData?.caragiverName ?: ""
            caregiverNumber.value = patientData?.caragiverNumber ?: ""
            allergiesValue.value = patientData?.allergy ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DADES PERSONALS",
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
            if (patientState == null) {
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
                return@Box
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
                                SectionHeader(text = "Informació del Pacient", latoFont)

                                EnhancedTextField(
                                    labelValue = "Nom",
                                    icon = Icons.Default.Person,
                                    textValue = nameValue,
                                    fontFamily = latoFont
                                )

                                EnhancedTextField(
                                    labelValue = "Cognoms",
                                    icon = Icons.Default.Person,
                                    textValue = surnameValue,
                                    fontFamily = latoFont
                                )

                                EnhancedBirthdayField(
                                    labelValue = "Data de naixement",
                                    icon = Icons.Default.Today,
                                    dateValue = birthdayValue,
                                    fontFamily = latoFont
                                )

                                EnhancedTextField(
                                    labelValue = "Adreça",
                                    icon = Icons.Default.LocationOn,
                                    textValue = addressValue,
                                    fontFamily = latoFont
                                )

                                EnhancedTextField(
                                    labelValue = "Llengua",
                                    icon = Icons.Default.Language,
                                    textValue = languageValue,
                                    fontFamily = latoFont
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Medical Information Section
                                SectionHeader(text = "Informació Mèdica", latoFont)

                                EnhancedMultilineField(
                                    labelValue = "Antecedents mèdics",
                                    icon = Icons.Default.EditNote,
                                    textValue = antecedentsMedics,
                                    fontFamily = latoFont
                                )

                                EnhancedMultilineField(
                                    labelValue = "Al·lèrgies",
                                    icon = Icons.Default.ReportProblem,
                                    textValue = allergiesValue,
                                    fontFamily = latoFont
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Caregiver Information Section
                                SectionHeader(text = "Informació del Cuidador", latoFont)

                                EnhancedTextField(
                                    labelValue = "Nom del cuidador",
                                    icon = Icons.Default.PermContactCalendar,
                                    textValue = caregiverName,
                                    fontFamily = latoFont
                                )

                                EnhancedTextField(
                                    labelValue = "Telèfon del cuidador",
                                    icon = Icons.Default.Call,
                                    textValue = caregiverNumber,
                                    fontFamily = latoFont
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        // Save Button
                        EnhancedSaveButton(
                            text = "Desar canvis",
                            isEnabled = true,
                            fontFamily = latoFont
                        ) {
                            val parsedDate = try {
                                dateFormat.parse(birthdayValue.value)
                            } catch (e: ParseException) {
                                null
                            }
                            // Action to save data
                            if (patientId != null) {
                                val updatePatientData = PatientState(
                                    historialNumber = patientId,
                                    name = nameValue.value,
                                    surname = surnameValue.value,
                                    direction = addressValue.value,
                                    dateBirth = parsedDate,
                                    language = languageValue.value,
                                    history = antecedentsMedics.value,
                                    caragiverName = caregiverName.value,
                                    caragiverNumber = caregiverNumber.value,
                                    allergy = allergiesValue.value,
                                    dateEntry = patientState?.dateEntry // Mantener la fecha de ingreso original
                                )

                                patientRemoteViewModel.updatePatient(patientId, updatePatientData)
                                updateRequested = true
                            }
                        }
                        LaunchedEffect(remoteApiMessage, updateRequested) {
                            if (updateRequested) {
                                when (remoteApiMessage) {
                                    is RemoteApiMessagePatient.Success -> {
                                        dialogMessage = "Data updated successfully."
                                        showSuccessDialog = true
                                        updateRequested = false // Reinicia la bandera
                                    }
                                    is RemoteApiMessagePatient.Error -> {
                                        dialogMessage = "Failing to update data."
                                        showErrorDialog = true
                                        updateRequested = false // Reinicia la bandera
                                    }
                                    RemoteApiMessagePatient.Loading -> Log.d("Loading Update", "Loading")
                                }
                            }
                        }
                        SuccessDialog(
                            showDialog = showSuccessDialog,
                            message = dialogMessage,
                            onDismiss = { showSuccessDialog = false }
                        )
                        ErrorDialog(
                            showDialog = showErrorDialog,
                            message = dialogMessage,
                            onDismiss = { showErrorDialog = false }
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
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Success") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ErrorDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
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
fun EnhancedTextField(
    labelValue: String,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily
) {
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
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
    labelValue: String,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily
) {
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
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
    labelValue: String,
    icon: ImageVector,
    dateValue: MutableState<String>,
    fontFamily: FontFamily
) {
    OutlinedTextField(
        value = dateValue.value,
        onValueChange = { dateValue.value = it },
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
fun EnhancedSaveButton(
    text: String,
    isEnabled: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
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
}