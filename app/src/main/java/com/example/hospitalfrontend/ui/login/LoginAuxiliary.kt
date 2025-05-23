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
import androidx.compose.ui.res.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.domain.model.user.AuxiliaryState
import com.example.hospitalfrontend.data.remote.viewmodel.AuxiliaryRemoteViewModel
import com.example.hospitalfrontend.ui.diagnosis.view.LatoFontFamily
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.theme.Primary

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
            LoginScreenAux(navController, auxiliaryRemoteViewModel, auxiliaryViewModel)
        }
    }
}

@Composable
fun LoginScreenAux(
    navController: NavController,
    auxiliaryRemoteViewModel: AuxiliaryRemoteViewModel,
    auxiliaryViewModel: AuxiliaryViewModel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(169, 199, 199))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val nunitoFont = FontFamily(Font(R.font.nunito_bold))

                // Title
                Text(
                    text = stringResource(id = R.string.login_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = nunitoFont
                    ),
                    color = colorResource(id = colorText),
                    textAlign = TextAlign.Center
                )

                // Logo
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(8.dp)
                ) {
                    Logo()
                }

                // Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AuxiliaryForm(navController, auxiliaryRemoteViewModel, auxiliaryViewModel)
                }
            }
        }
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
    var dialogMessageResId by remember { mutableStateOf<Int?>(null) }

    //To hide the login button
    val isValid = rememberSaveable(auxiliaryId.value) {
        //trims() to remove the white space and .isNotEmpty for that isn't empty
        auxiliaryId.value.trim().isNotEmpty()
    }
    // State for a validation button login
    var validButton by rememberSaveable { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AuxiliaryNumberInput(auxiliaryId = auxiliaryId, onValidationChanged = { valid ->
            validButton = valid
        })
        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(50.dp))
        SubmitButtonLogin(textId = stringResource(id = R.string.button_login), inputValid = isValid)
        {
            val id = auxiliaryId.value.trim().toIntOrNull()
            if (id != null) {
                auxiliaryRemoteViewModel.loginAuxiliary(id)
                auxiliar.id = id
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = stringResource(id = R.string.dialog_ok)) //'OK'
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.dialog_error)) //text = "ERROR: Login"
                },
                text = {
                    dialogMessageResId?.let {
                        Text(text = stringResource(id = it))
                    } ?: Text(text = dialogMessage)
                }
            )
        }
        LaunchedEffect(messageApi) {
            when (messageApi) {
                is RemoteApiMessageAuxiliary.Success -> {
                    auxiliaryRemoteViewModel.clearApiMessage()
                    auxiliaryViewModel.loginAuxiliary(messageApi.message)
                    navController.navigate("home")
                }

                is RemoteApiMessageAuxiliary.Error -> {
                    dialogMessageResId = R.string.connection_error
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
            .fillMaxWidth(0.7f)
            .height(56.dp),
        enabled = inputValid,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(151, 199, 150),
                            Color(151, 199, 150).copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            val nunitoFont = FontFamily(Font(R.font.nunito_medium))
            Text(
                text = textId,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = nunitoFont,
                color = Color.White
            )
        }
    }
}


@Composable
fun AuxiliaryNumberInput(
    auxiliaryId: MutableState<String>,
    onValidationChanged: (Boolean) -> Unit
) {
    val latoFont = FontFamily(Font(R.font.lato_regular))
    val labelText = stringResource(id = R.string.aux_number_label)
    InputFieldAuxiliar(
        valueState = auxiliaryId,
        labelId = labelText,
        keyboardType = KeyboardType.Number,
        textStyle = TextStyle(fontFamily = latoFont, fontSize = 18.sp),
        onValidationChanged = onValidationChanged

    )
}

@Composable
fun InputFieldAuxiliar(
    valueState: MutableState<String>,
    labelId: String,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType,
    textStyle: TextStyle = TextStyle(),
    onValidationChanged: (Boolean) -> Unit
) {
    var showError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = valueState.value,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                    valueState.value = newValue
                    showError = false
                    onValidationChanged(true)
                } else {
                    showError = true
                    onValidationChanged(false)
                }
            },
            label = {
                Text(
                    text = labelId,
                    fontFamily = LatoFontFamily,
                    style = TextStyle(fontSize = 18.sp)
                )
            },
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
        if (showError) {
            Text(
                text = stringResource(id = R.string.regex),
                color = Color.Red,
                fontFamily = LatoFontFamily,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}