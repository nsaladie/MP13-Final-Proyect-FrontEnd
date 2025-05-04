package com.example.hospitalfrontend.ui.nurses.view

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.R.color.*
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageNurse
import com.example.hospitalfrontend.data.remote.viewmodel.NurseRemoteViewModel
import com.example.hospitalfrontend.domain.model.user.NurseState
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.*

@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value, modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp), style = TextStyle(
            fontSize = 24.sp, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal
        ), color = colorResource(id = colorText), textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value, modifier = Modifier
            .fillMaxWidth()
            .heightIn(), style = TextStyle(
            fontSize = 30.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
        ), color = colorResource(id = colorText), textAlign = TextAlign.Center
    )
}

@Composable
fun MyTextField(labelValue: String, painterResources: Painter, textValue: MutableState<String>) {
    OutlinedTextField(label = { Text(text = labelValue) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
        ),
        keyboardOptions = KeyboardOptions.Default,
        value = textValue.value,
        onValueChange = { textValue.value = it },
        leadingIcon = {
            Icon(
                painter = painterResources, contentDescription = "", modifier = Modifier.size(18.dp)
            )
        })
}

@Composable
fun DateTextField(labelValue: String, painterResources: Painter, dateValue: MutableState<String>) {
    val datePattern =
        Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$")  // Format dd/MM/yyyy
    val isDateValid = datePattern.matches(dateValue.value)

    // Check if the date is empty
    val isDateEmpty = dateValue.value.isEmpty()

    OutlinedTextField(
        label = { Text(text = labelValue) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
            // Set the indicator color to red only if the date is invalid and not empty
            unfocusedIndicatorColor = if (isDateEmpty || isDateValid) Color.DarkGray else Color.Red,
        ),
        keyboardOptions = KeyboardOptions.Default,
        value = dateValue.value,
        onValueChange = { dateValue.value = it },
        leadingIcon = {
            Icon(
                painter = painterResources, contentDescription = "", modifier = Modifier.size(18.dp)
            )
        },
        isError = !isDateValid && !isDateEmpty // Only show error if the date is invalid and not empty
    )

    // Show error if the date is invalid and not empty
    if (!isDateValid && dateValue.value.isNotEmpty()) {
        Text(
            text = "Please enter a valid date (dd/MM/yyyy)", color = Color.Red, fontSize = 12.sp
        )
    }
}

@Composable
fun PasswordTextField(
    labelValue: String, painterResources: Painter, passwordValue: MutableState<String>
) {
    val passwordVisibility = rememberSaveable {
        mutableStateOf(false)
    }

    // Validation for the password
    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{4,}$")
    val isPasswordValid = passwordPattern.matches(passwordValue.value)

    // Check if the password is empty
    val isPasswordEmpty = passwordValue.value.isEmpty()

    OutlinedTextField(
        label = { Text(text = labelValue) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
            // Set the indicator color to red only if the password is invalid and not empty
            unfocusedIndicatorColor = if (isPasswordEmpty || isPasswordValid) Color.DarkGray else Color.Red,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        value = passwordValue.value,
        onValueChange = { passwordValue.value = it },
        leadingIcon = {
            Icon(
                painter = painterResources, contentDescription = "", modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            val iconImage = if (passwordVisibility.value) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            val description = if (passwordVisibility.value) {
                "Hide password"
            } else {
                "Show password"
            }

            IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }
        },
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !isPasswordValid && !isPasswordEmpty // Only show error if password is invalid and not empty
    )

    // Show error if the password is invalid and not empty
    if (!isPasswordValid && passwordValue.value.isNotEmpty()) {
        Text(
            text = "Password must be at least 4 characters long, include a capital letter, a lowercase letter, and a number.",
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SpecialityDropdown(
    viewModel: NurseViewModel,
    selectedSpeciality: MutableState<String>
) {
    val specialityList by viewModel.specialityNurse.collectAsState()
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedSpeciality.value,
            onValueChange = { },
            label = { Text("Speciality") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { isDropdownExpanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Open dropdown")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Primary,
                cursorColor = Primary,
                focusedLabelColor = Primary,
            )
        )

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }) {
            specialityList.forEach { speciality ->
                DropdownMenuItem(text = { Text(speciality) }, onClick = {
                    selectedSpeciality.value = speciality
                    isDropdownExpanded = false
                })
            }
        }
    }
}


@Composable
fun ButtonComponent(value: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        enabled = enabled,
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
                text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CreateNursePage(
    navController: NavController,
    nurseViewModel: NurseViewModel,
    remoteViewModel: NurseRemoteViewModel
) {
    val messageApi = remoteViewModel.remoteApiMessage.value
    // State for controller of dialog visibility
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    val firstName = rememberSaveable { mutableStateOf("") }
    val lastName = rememberSaveable { mutableStateOf("") }
    val age = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val selectedSpeciality = rememberSaveable { mutableStateOf("") }

    val showErrors by rememberSaveable { mutableStateOf(false) }

    val isEmailValid =
        Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matches(email.value)

    fun isFormValid(): Boolean {
        return firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && age.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() && selectedSpeciality.value.isNotEmpty() && isEmailValid // Optionally check if email is valid
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = "Hello there, ")
            HeadingTextComponent(value = "Create an Account ")
            Spacer(modifier = Modifier.height(20.dp))

            // Name Field
            MyTextField(
                labelValue = "First Name",
                painterResources = painterResource(id = R.drawable.icon_user),
                textValue = firstName
            )
            // Surname Field
            MyTextField(
                labelValue = "Last Name",
                painterResources = painterResource(id = R.drawable.icon_user),
                textValue = lastName
            )
            // Age Field
            DateTextField(
                labelValue = "Birth Date",
                painterResources = painterResource(id = R.drawable.icon_user),
                dateValue = age
            )
            // Specialty Field
            SpecialityDropdown(nurseViewModel, selectedSpeciality)

            // Email Field
            MyTextField(
                labelValue = "Email",
                painterResources = painterResource(id = R.drawable.icon_mail),
                textValue = email
            )
            if (showErrors && !isEmailValid) {
                Text("Invalid email", color = Color.Red, fontSize = 12.sp)
            }

            // Password Field
            PasswordTextField(
                labelValue = "Password",
                painterResources = painterResource(id = R.drawable.icon_password),
                passwordValue = password
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Pass to the view of Login
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Already have an account? ")
                Text(text = "Login",
                    modifier = Modifier
                        .clickable { navController.navigate("login") }
                        .padding(start = 2.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))

            // Button SignUp
            ButtonComponent(value = "Register", enabled = isFormValid()) {
                val nurse = NurseState(
                    id = 0,
                    name = firstName.value,
                    surname = lastName.value,
                    age = age.value,
                    email = email.value,
                    password = password.value,
                    speciality = selectedSpeciality.value
                )
                remoteViewModel.createNurse(nurse)
            }

            // Show dialog if it is needed
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    },
                    title = {
                        Text(text = "Fail creating a new account")
                    },
                    text = {
                        Text(text = dialogMessage)
                    }
                )
            }

            LaunchedEffect(messageApi) {
                when (messageApi) {
                    is RemoteApiMessageNurse.Success -> {
                        nurseViewModel.addNurse(messageApi.message)
                        remoteViewModel.clearApiMessage() // Change the message to Loading to avoid repeated messages when user is logout
                        navController.navigate("home") {
                            popUpTo("create") { inclusive = true }
                        }
                    }

                    is RemoteApiMessageNurse.Error -> {
                        // Show dialog with a specific message
                        dialogMessage = "Error creating a new account"
                        showDialog = true // Show the dialog
                    }

                    RemoteApiMessageNurse.Loading -> Log.d("Loading", "Loading")
                }
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateNursePagePreview() {
    val navController = rememberNavController()
    val nurseViewModel = NurseViewModel()
    val remoteViewModel = NurseRemoteViewModel()

    HospitalFrontEndTheme {
        CreateNursePage(navController, nurseViewModel, remoteViewModel)
    }
}
