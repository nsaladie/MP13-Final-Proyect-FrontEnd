import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PermContactCalendar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.network.PatientRemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import com.example.hospitalfrontend.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PersonalData(
    navController: NavController,
    patientRemoteViewModel: PatientRemoteViewModel,
    patientViewModel: PatientViewModel,
    patientId: Int,
) {
    var patientState by remember { mutableStateOf<PatientState?>(null) }

    LaunchedEffect(patientId) {
        patientRemoteViewModel.getPatientById(patientId, patientViewModel)

    }

    LaunchedEffect(patientViewModel.patientState) {
        patientViewModel.patientState.collect { newState ->
            patientState = newState
        }
    }

    if (patientState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        //cross to return to the menu
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 30.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Title
        val nunitoFont = FontFamily(Font(R.font.nunito_bold))
        Text(
            text = "DADES PERSONALS",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = nunitoFont
            ),
            color = colorResource(id = colorText),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Form data
        val nameValue = rememberSaveable { mutableStateOf(patientState?.name ?: "") }
        val surnameValue = rememberSaveable { mutableStateOf(patientState?.surname ?: "") }
        val addressValue = rememberSaveable { mutableStateOf(patientState?.direction ?: "") }
        val birthdayValue = rememberSaveable { mutableStateOf(patientState?.dateBirth ?.let { dateFormat.format(it)?:""}) }
        val languageValue = rememberSaveable { mutableStateOf(patientState?.language ?: "") }
        val antecedentsMedics = rememberSaveable { mutableStateOf(patientState?.history ?: "") }
        val caregiverName = rememberSaveable { mutableStateOf(patientState?.caragiverName ?: "") }
        val caregiverNumber =
            rememberSaveable { mutableStateOf(patientState?.caragiverNumber ?: "") }
        val allergiesValue = rememberSaveable { mutableStateOf(patientState?.allergy ?: "") }
        DataForm(
            nameValue = nameValue,
            surnameValue = surnameValue,
            birthdayValue = birthdayValue,
            addressValue = addressValue,
            languageValue = languageValue,
            antecedentsMedics = antecedentsMedics,
            allergiesValue = allergiesValue,
            caregiverName = caregiverName,
            caregiverNumber = caregiverNumber
        )
    }
}


@Composable
fun DataForm(
    nameValue: MutableState<String>,
    surnameValue: MutableState<String>,
    birthdayValue: MutableState<String?>,
    addressValue: MutableState<String>,
    languageValue: MutableState<String>,
    antecedentsMedics: MutableState<String>,
    allergiesValue: MutableState<String>,
    caregiverName: MutableState<String>,
    caregiverNumber: MutableState<String>,
) {
    val latoFont = FontFamily(Font(R.font.lato_regular))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(labelValue = "Nom", icon = Icons.Default.Person, textValue = nameValue, fontFamily = latoFont)
        TextField(labelValue = "Cognoms", icon = Icons.Default.Person, textValue = surnameValue,fontFamily = latoFont)
        BirthdayInput(
            labelValue = "Birthday",
            icon = Icons.Default.Today,
            dateValue = birthdayValue,
            fontFamily = latoFont
        )
        TextField(labelValue = "Adreça", icon = Icons.Default.LocationOn, textValue = addressValue,fontFamily = latoFont)
        TextField(labelValue = "Llengua", icon = Icons.Default.Person, textValue = languageValue, fontFamily = latoFont)
        TextField(
            labelValue = "Antecedents mèdics",
            icon = Icons.Default.Assignment,
            textValue = antecedentsMedics,fontFamily = latoFont
        )
        TextField(
            labelValue = "Al·lèrgies",
            icon = Icons.Default.Person,
            textValue = allergiesValue,fontFamily = latoFont
        )
        TextField(
            labelValue = "Dades del cuidador:nom",
            icon = Icons.Default.PermContactCalendar,
            textValue = caregiverName,fontFamily = latoFont
        )
        TextField(
            labelValue = "Dades del cuidador:teléfon",
            icon = Icons.Default.Call,
            textValue = caregiverNumber,fontFamily = latoFont
        )
        Spacer(modifier = Modifier.height(25.dp))
        // Botón para guardar datos
        SaveDataButton(textId = "Desar", inputValid = true) {
            // Acción para guardar los datos antes de navegar
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TextField(
    labelValue: String,
    icon: ImageVector,
    textValue: MutableState<String>,
    fontFamily: FontFamily
) {
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        label = { Text(text = labelValue) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Color.DarkGray,
            unfocusedIndicatorColor = Color.Gray
        )
    )
}

@Composable
fun BirthdayInput(
    labelValue: String,
    icon: ImageVector,
    dateValue: MutableState<String?>,
    fontFamily: FontFamily
) {
    val datePattern = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$")
    val isDateValid = datePattern.matches(dateValue.value.toString())
    val isDateEmpty = dateValue.value

    dateValue.value?.let {
        OutlinedTextField(
        value = it,
        onValueChange = { dateValue.value = it },
        label = { Text(text = labelValue) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        //isError = !isDateValid && !isDateEmpty,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Color.DarkGray,
            //unfocusedIndicatorColor = if (isDateEmpty || isDateValid) Color.Gray else Color.Red
        )
    )
    }
}


@Composable
fun SaveDataButton(
    textId: String,
    inputValid: Boolean,
    onClick: () -> Unit // Agregamos este parámetro
) {
    val customGreen = Color(169, 199, 199)
    val latoFont = FontFamily(Font(R.font.lato_light))

    Button(
        onClick = onClick, // Se ejecutará la acción pasada por parámetro
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(48.dp),
        enabled = false,
        colors = ButtonDefaults.buttonColors(containerColor = customGreen),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = textId,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = latoFont,
            color = Color.Black
        )
    }
}


