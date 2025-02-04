package com.example.hospitalfrontend.ui.nurses.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.NurseState
import com.example.hospitalfrontend.network.RemoteApiMessageBoolean
import com.example.hospitalfrontend.network.RemoteApiMessageNurse
import com.example.hospitalfrontend.network.RemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme
import com.example.hospitalfrontend.ui.theme.Primary

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(
    navController: NavController,
    nurseViewModel: NurseViewModel,
    remoteViewModel: RemoteViewModel
) {
    val nurseState = nurseViewModel.nurseState.value

    var profileImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(nurseState?.id) {
        nurseState?.id?.let { id ->
            profileImageBitmap = remoteViewModel.getPhotoById(id)
        }
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { profileImageUri = it }
        }

    val nameValue = rememberSaveable { mutableStateOf(nurseState?.name ?: "") }
    val surnameValue = rememberSaveable { mutableStateOf(nurseState?.surname ?: "") }
    val emailValue = rememberSaveable { mutableStateOf(nurseState?.email ?: "") }
    val birthdayValue = rememberSaveable { mutableStateOf(nurseState?.age ?: "") }
    val specialityValue = rememberSaveable { mutableStateOf(nurseState?.speciality ?: "") }
    val passwordValue = rememberSaveable { mutableStateOf("") }

    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    val remoteApiMessageUploadPhoto = remoteViewModel.remoteApiMessageUploadPhoto.value

    LaunchedEffect(remoteApiMessageUploadPhoto) {
        when (remoteApiMessageUploadPhoto) {
            is RemoteApiMessageBoolean.Success -> {
                dialogMessage = "Photo uploaded successfully."
                showSuccessDialog = true
                remoteViewModel.clearApiMessage()
            }

            is RemoteApiMessageBoolean.Error -> {
                dialogMessage = "Failing to upload photo."
                showErrorDialog = true
                remoteViewModel.clearApiMessage()
            }

        }
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8282E1),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                ProfileHeader(
                    navController,
                    nurseState,
                    nameValue,
                    surnameValue,
                    birthdayValue,
                    emailValue,
                    passwordValue,
                    specialityValue,
                    remoteViewModel
                )
                Spacer(modifier = Modifier.height(16.dp))
                ProfileImage(
                    profileImageUri,
                    profileImageBitmap,
                    imagePickerLauncher,
                    nurseState,
                    remoteViewModel,
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProfileDetails(nurseState)
                Spacer(modifier = Modifier.height(10.dp))
                ProfileForm(
                    nameValue,
                    surnameValue,
                    emailValue,
                    birthdayValue,
                    passwordValue,
                    specialityValue,
                    nurseViewModel
                )
                Spacer(modifier = Modifier.height(20.dp))
                ProfileActions(remoteViewModel, nurseViewModel, nurseState)
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


@Composable
fun ProfileHeader(
    navController: NavController,
    nurseState: NurseState?,
    nameValue: MutableState<String>,
    surnameValue: MutableState<String>,
    birthdayValue: MutableState<String>,
    emailValue: MutableState<String>,
    passwordValue: MutableState<String>,
    specialityValue: MutableState<String>,
    remoteViewModel: RemoteViewModel,
) {
    val remoteApiMessage = remoteViewModel.remoteApiMessage.value
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    val isEmailValid =
        Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matches(emailValue.value)

    fun isFormValid(): Boolean {
        return nameValue.value.isNotEmpty() && surnameValue.value.isNotEmpty() && emailValue.value.isNotEmpty() && birthdayValue.value.isNotEmpty() && passwordValue.value.isNotEmpty() && specialityValue.value.isNotEmpty() && isEmailValid
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(text = "Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        IconButton(enabled = isFormValid(), onClick = {
            if (nurseState?.id != null) {
                val updateNurse = NurseState(
                    id = nurseState.id,
                    name = nameValue.value,
                    surname = surnameValue.value,
                    age = birthdayValue.value,
                    email = emailValue.value,
                    password = passwordValue.value,
                    speciality = specialityValue.value
                )
                remoteViewModel.updateNurse(nurseState.id, updateNurse)
            }
        }
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save Change",
                tint = if (isFormValid()) Color.Green else Color.LightGray,
                modifier = Modifier.size(30.dp)
            )
        }
        LaunchedEffect(remoteApiMessage) {
            when (remoteApiMessage) {
                is RemoteApiMessageNurse.Success -> {
                    dialogMessage = "Data updated successfully."
                    showSuccessDialog = true
                    remoteViewModel.clearApiMessage()
                }

                is RemoteApiMessageNurse.Error -> {
                    dialogMessage = "Failing to update data."
                    showErrorDialog = true
                    remoteViewModel.clearApiMessage()
                }

                RemoteApiMessageNurse.Loading -> Log.d("Loading Update", "Loading")
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


@Composable
fun ProfileImage(
    profileImageUri: Uri?,
    profileImageBitmap: Bitmap?,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    nurseState: NurseState?,
    remoteViewModel: RemoteViewModel,
) {
    val context = LocalContext.current
    val imageToShow = profileImageUri?.let { rememberAsyncImagePainter(it) }
        ?: profileImageBitmap?.let { BitmapPainter(it.asImageBitmap()) }
        ?: painterResource(R.drawable.nurse_profile)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = imageToShow,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.offset(x = 85.dp)) {
            IconButton(
                enabled = profileImageUri != null,
                onClick = {
                    profileImageUri?.let { uri ->
                        nurseState?.id?.let { id ->
                            remoteViewModel.uploadPhoto(id, uri, context)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAs,
                    contentDescription = "Save Image",
                    tint = Color.Black,
                )
            }
        }
    }
}


@Composable
fun ProfileDetails(nurseState: NurseState?) {
    Text(
        text = "${nurseState?.name} ${nurseState?.surname}",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Text(text = nurseState?.email ?: "", fontSize = 16.sp, color = Color.White.copy(alpha = 0.8f))
}

@Composable
fun ProfileForm(
    nameValue: MutableState<String>,
    surnameValue: MutableState<String>,
    emailValue: MutableState<String>,
    birthdayValue: MutableState<String>,
    passwordValue: MutableState<String>,
    specialityValue: MutableState<String>,
    nurseViewModel: NurseViewModel
) {
    MyTextUpdateField(labelValue = "Name", icon = Icons.Default.Person, textValue = nameValue)
    MyTextUpdateField(
        labelValue = "Surname",
        icon = Icons.Default.Person,
        textValue = surnameValue
    )
    MyTextUpdateField(labelValue = "Email", icon = Icons.Default.Email, textValue = emailValue)
    DateTextUpdateField(
        labelValue = "Birthday",
        icon = Icons.Default.Today,
        dateValue = birthdayValue
    )
    PasswordTextUpdateField(
        labelValue = "Password",
        icon = Icons.Default.Password,
        passwordValue = passwordValue
    )
    SpecialityUpdateDropdown(nurseViewModel, specialityValue)
}

@Composable
fun ProfileActions(
    remoteViewModel: RemoteViewModel,
    nurseViewModel: NurseViewModel,
    nurseState: NurseState?,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Button(
        onClick = { nurseViewModel.disconnectNurse() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = "LogOut",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "LogOut", fontSize = 18.sp, color = Color.White)
    }
    Button(
        onClick = {
            showDeleteDialog = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Delete", fontSize = 18.sp, color = Color.White)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Confirm Deletion")
            },
            text = {
                Text(text = "Are you sure you want to delete your account?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        remoteViewModel.deleteNurse(nurseState!!.id)
                        remoteViewModel.clearApiMessage()
                        nurseViewModel.deleteNurse()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MyTextUpdateField(
    labelValue: String,
    icon: ImageVector,
    textValue: MutableState<String>
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
fun DateTextUpdateField(
    labelValue: String,
    icon: ImageVector,
    dateValue: MutableState<String>
) {
    val datePattern = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$")
    val isDateValid = datePattern.matches(dateValue.value)
    val isDateEmpty = dateValue.value.isEmpty()

    OutlinedTextField(
        value = dateValue.value,
        onValueChange = { dateValue.value = it },
        label = { Text(text = labelValue) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        isError = !isDateValid && !isDateEmpty,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Color.DarkGray,
            unfocusedIndicatorColor = if (isDateEmpty || isDateValid) Color.Gray else Color.Red
        )
    )

    if (!isDateValid && !isDateEmpty) {
        Text(
            text = "Please enter a valid date (dd/MM/yyyy)",
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PasswordTextUpdateField(
    labelValue: String,
    icon: ImageVector,
    passwordValue: MutableState<String>
) {
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{4,}$")
    val isPasswordValid = passwordPattern.matches(passwordValue.value)
    val isPasswordEmpty = passwordValue.value.isEmpty()

    OutlinedTextField(
        value = passwordValue.value,
        onValueChange = { passwordValue.value = it },
        label = { Text(text = labelValue) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        trailingIcon = {
            val visibilityIcon =
                if (passwordVisibility.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                Icon(imageVector = visibilityIcon, contentDescription = null)
            }
        },
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        isError = !isPasswordValid && !isPasswordEmpty,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Color.DarkGray,
            unfocusedIndicatorColor = if (isPasswordEmpty || isPasswordValid) Color.Gray else Color.Red
        )
    )

    if (!isPasswordValid && !isPasswordEmpty) {
        Text(
            text = "Password must include uppercase, lowercase, and a number.",
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SpecialityUpdateDropdown(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { isDropdownExpanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Open dropdown")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Primary,
                cursorColor = Primary,
                focusedLabelColor = Color.DarkGray,
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


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    HospitalFrontEndTheme {
        val navController = rememberNavController()
        ProfileScreen(
            navController,
            nurseViewModel = NurseViewModel(),
            remoteViewModel = RemoteViewModel()
        )
    }
}