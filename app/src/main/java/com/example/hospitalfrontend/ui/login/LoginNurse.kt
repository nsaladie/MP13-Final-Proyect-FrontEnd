package com.example.hospitalfrontend.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.colorText
import com.example.hospitalfrontend.model.LoginRequest
import com.example.hospitalfrontend.network.RemoteApiMessageNurse
import com.example.hospitalfrontend.network.NurseRemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme
import com.example.hospitalfrontend.ui.theme.Primary
import com.example.hospitalfrontend.ui.theme.Secundary

@Composable
fun HospitalLoginScreen(
    nurseViewModel: NurseViewModel, navController: NavController, remoteViewModel: NurseRemoteViewModel
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            LoginOrRegisterScreen(navController, nurseViewModel, remoteViewModel)

        }
    }
}

@Composable
fun LoginOrRegisterScreen(
    navController: NavController, nurseViewModel: NurseViewModel, remoteViewModel: NurseRemoteViewModel
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image()
        Text(
            text = "Login", modifier = Modifier
                .fillMaxWidth()
                .heightIn(), style = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
            ), color = colorResource(id = colorText), textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        UserForm(nurseViewModel, navController, remoteViewModel)
    }
}

@Composable
fun ToggleLoginRegisterText(navController: NavController) {

    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Don't have an account?")
        Text(text = "Register",
            modifier = Modifier
                .clickable { navController.navigate("create") }
                .padding(start = 5.dp))
    }
}

//This function is only for the image
@Composable
fun Image() {
    Image(
        painter = painterResource(id = R.drawable.login),
        contentDescription = "Login screen image",
        modifier = Modifier.size(100.dp)
    )
}


@Composable
fun UserForm(
    nurseViewModel: NurseViewModel, navController: NavController, remoteViewModel: NurseRemoteViewModel
) {
    val messageApi = remoteViewModel.remoteApiMessage.value
    // State for controller of dialog visibility
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    //Create variables for the form
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val password = rememberSaveable {
        mutableStateOf("")
    }
    //If the password is visible or not
    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }

    //To hide the login button
    val isValid = rememberSaveable(email.value, password.value) {
        //trims() to remove the white space and .isNotEmpty for that isn't empty
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //The text field email
        EmailInput(
            emailState = email
        )
        //The text field password
        PasswordInput(
            passwordState = password, labelId = "Password", passwordVisible = passwordVisible
        )

        Spacer(modifier = Modifier.height(20.dp))
        // Go to register screen
        ToggleLoginRegisterText(navController)
        Spacer(modifier = Modifier.height(50.dp))

        SubmitButton(
            textId = "Login", inputValid = isValid
        ) {
            val dataLogin = LoginRequest(email.value, password.value)
            remoteViewModel.loginNurse(dataLogin)
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
                is RemoteApiMessageNurse.Success -> {
                    // Call to the nurseViewModel to save the nurse in the database
                    nurseViewModel.loginNurse(messageApi.message)
                    remoteViewModel.clearApiMessage() // Change the message to Loading to avoid repeated messages when user is logout
                    /*navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }*/
                }

                is RemoteApiMessageNurse.Error -> {
                    // Show dialog with a specific message
                    dialogMessage = "Incorrect Email or Password"
                    showDialog = true // Show the dialog
                }

                RemoteApiMessageNurse.Loading -> Log.d("Loading", "Loading")
            }
        }

    }
}

@Composable
fun SubmitButton(
    textId: String, inputValid: Boolean, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        enabled = inputValid,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Secundary, Primary)
                    ), shape = RoundedCornerShape(50.dp)
                ), contentAlignment = Alignment.Center
        ) {
            Text(
                text = textId, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>, labelId: String, passwordVisible: MutableState<Boolean>
) {
    //If the value password is  true then the password is visible
    val visualTransformation = if (passwordVisible.value) VisualTransformation.None
    //If the value password is false the the password is invisible
    else PasswordVisualTransformation()
    OutlinedTextField(value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
        ),
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        //Hide the password
        visualTransformation = visualTransformation,
        //create the icon for hide or show the password
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            }
        })
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    //The icon show it eye open or eye close
    val image = if (passwordVisible.value) Icons.Default.VisibilityOff
    else Icons.Default.Visibility
    //When we write the password we can see the icon
    IconButton(onClick = {
        //We active the icon and when we click the password is visible or invisible
        passwordVisible.value = !passwordVisible.value
    }) {
        Icon(
            imageVector = image, contentDescription = ""
        )
    }
}

//This function is of the text field email
@Composable
fun EmailInput(
    emailState: MutableState<String>,
    //I used labelId so that there is a label that puts email in its text field.
    labelId: String = "Email"
) {
    // I have created this function so that I can create other non-email fields later.
    InputField(
        valueState = emailState, labelId = labelId,/*I have created this variable so that the
        @ sign appears when the nurse enters her e-mail.*/
        keyboardType = KeyboardType.Email
    )
}

//This function is for the layout of the texts fields.
@Composable
fun InputField(
    valueState: MutableState<String>,
    labelId: String,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType
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
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    val navController = rememberNavController()
    val nurseViewModel = NurseViewModel()
    val remoteViewModel = NurseRemoteViewModel()

    HospitalFrontEndTheme {
        HospitalLoginScreen(nurseViewModel, navController, remoteViewModel)
    }
}

