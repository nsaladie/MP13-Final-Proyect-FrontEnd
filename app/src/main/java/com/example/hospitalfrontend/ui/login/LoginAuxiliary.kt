package com.example.hospitalfrontend.ui.login

import android.util.Log
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.model.AuxiliaryState
import com.example.hospitalfrontend.network.AuxiliaryRemoteViewModel
import com.example.hospitalfrontend.network.NurseRemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme
import com.example.hospitalfrontend.ui.theme.Primary
import com.example.hospitalfrontend.ui.theme.Secundary

@Composable
fun LoginScreenAuxiliary(
    navController: NavController,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    auxiliaryViewModel: AuxiliaryViewModel
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginScreenNurse(navController, auxiliaryRemoteViewModel, auxiliaryViewModel)
        }
    }
}

@Composable
fun LoginScreenNurse(
    navController: NavController,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    auxiliaryViewModel: AuxiliaryViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val nunitoFont = FontFamily(Font(R.font.nunito_bold))
        Text(
            text = "Login",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = nunitoFont
            ),
            color = colorResource(id = colorText),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Logo()
        AuxiliaryForm(navController, auxiliaryRemoteViewModel, auxiliaryViewModel)
    }
}

@Composable
fun Logo() {
    androidx.compose.foundation.Image(
        painter = painterResource(id = R.drawable.health_nest),
        contentDescription = "Logo image",
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(10.dp))
    )
    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun AuxiliaryForm(
    navController: NavController,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    auxiliaryViewModel: AuxiliaryViewModel
) {
    var auxiliar = AuxiliaryState()
    //Create variables for the form
    val auxiliaryId = rememberSaveable { mutableStateOf("") }
    val messageApi = auxiliaryRemoteViewModel.remoteApiMessageAuxiliary.value
    // State for controller of dialog visibility
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }
    //To hide the login button
    val isValid = rememberSaveable(auxiliaryId.value) {
        //trims() to remove the white space and .isNotEmpty for that isn't empty
        auxiliaryId.value.trim().isNotEmpty()
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AuxiliaryNumberInput(auxiliaryId = auxiliaryId)
        Spacer(modifier = Modifier.height(20.dp))
        // Aquí deberías agregar ToggleLoginRegisterText(navController) si está definida
        Spacer(modifier = Modifier.height(50.dp))
        SubmitButtonLogin(textId = "Login", inputValid = isValid)
        {

            val id = auxiliaryId.value.trim().toIntOrNull()
            if (id != null) {
                auxiliaryRemoteViewModel.loginAuxiliary(id)
                auxiliar.id = id
            }
        }
        // Mostrar el diálogo si es necesario
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
                title = {
                    Text(text = "ERROR: Login")
                },
                text = {
                    Text(text = dialogMessage)
                }
            )
        }
        LaunchedEffect(messageApi) {
            when (messageApi) {
                is RemoteApiMessageAuxiliary.Success -> {
                        auxiliaryRemoteViewModel.clearApiMessage()
                        auxiliaryViewModel.loginAuxiliary(auxiliar)
                        navController.navigate("home")
                }

                is RemoteApiMessageAuxiliary.Error -> {
                    dialogMessage = "Error en la conexión"
                    showDialog = true
                }

                RemoteApiMessageAuxiliary.Loading -> Log.d("Loading", "Cargando...")
                else -> {}
            }
        }
    }
}

@Composable
fun SubmitButtonLogin(textId: String, inputValid: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(48.dp),
        enabled = inputValid,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(listOf(Secundary, Primary)),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val latoFont = FontFamily(Font(R.font.lato_light))
            Text(
                text = textId,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = latoFont,
                color = Color.White
            )
        }
    }
}


@Composable
fun AuxiliaryNumberInput(
    auxiliaryId: MutableState<String>,
    labelId: String = "Number"
) {
    val latoFont = FontFamily(Font(R.font.lato_light_italic))

    InputFieldAuxiliar(
        valueState = auxiliaryId,
        labelId = labelId,
        keyboardType = KeyboardType.Number,
        textStyle = TextStyle(fontFamily = latoFont)
    )
}

//This function is for the layout of the texts fields.
@Composable
fun InputFieldAuxiliar(
    valueState: MutableState<String>,
    labelId: String,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType,
    textStyle: TextStyle = TextStyle()
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        textStyle = textStyle
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginAuxiliar() {
    val navController = rememberNavController()
    val auxiliaryRemoteViewModel = AuxiliaryRemoteViewModel()
    val auxiliaryViewModel = AuxiliaryViewModel()
    HospitalFrontEndTheme {

        LoginScreenAuxiliary(navController, auxiliaryRemoteViewModel, auxiliaryViewModel)
    }
}
