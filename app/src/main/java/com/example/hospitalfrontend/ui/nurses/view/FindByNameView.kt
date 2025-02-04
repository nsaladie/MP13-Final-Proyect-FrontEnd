package com.example.hospitalfrontend.ui.nurses.view

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.hospitalfrontend.model.NurseState
import com.example.hospitalfrontend.network.RemoteApiMessageNurse
import com.example.hospitalfrontend.network.RemoteViewModel


@Preview
@Composable
fun MySearchPreview() {
    HospitalFrontEndTheme {
        val navController = rememberNavController()
        val nurseViewModel = NurseViewModel()
        val remoteViewModel = RemoteViewModel()
        FindScreen(
            navController,
            remoteViewModel,
            nurseViewModel,
        )
    }

}

@Composable
fun TextField(labelValue: String, onValueChange: (String) -> Unit, textFieldValue: String = "") {
    OutlinedTextField(label = { Text(text = labelValue) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Primary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
        ),
        keyboardOptions = KeyboardOptions.Default,
        value = textFieldValue,
        onValueChange = { onValueChange(it) })
}

@Composable
fun ListSearchNurse(nurse: NurseState, remoteViewModel: RemoteViewModel) {
    val age by remember(nurse.age) { // Calculate age only when nurse.age changes
        mutableIntStateOf(calculateAge(nurse.age))
    }
    // Create variable to save the result of the response
    var profileImageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    val profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    // Call to the method to get the profile image
    LaunchedEffect(nurse.id) {
        profileImageBitmap = remoteViewModel.getPhotoById(nurse.id)
    }
    // Create a painter to display the image
    val imageToShow = profileImageUri?.let { rememberAsyncImagePainter(it) }
        ?: profileImageBitmap?.let { BitmapPainter(it.asImageBitmap()) }
        ?: painterResource(R.drawable.nurse_profile)

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = imageToShow,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = "ID Nurse: ${nurse.id}")
                Text(text = "Name: ${nurse.name}")
                Text(text = "Surname: ${nurse.surname}")
                Text(text = "Date Bird: ${nurse.age} ($age years old)")
                Text(text = "Email: ${nurse.email}")
                Text(text = "Speciality: ${nurse.speciality}")
            }
        }
    }
}

@Composable
fun FindScreen(
    navController: NavController, remoteApiMessage: RemoteViewModel, nurseViewModel: NurseViewModel
) {
    val currentSearchName by nurseViewModel.currentSearchName.collectAsState()
    val message = remoteApiMessage.remoteApiMessage.value

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .size(24.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close Button",
                    tint = colorResource(id = R.color.colorText)
                )
            }

            Text(
                "Find Nurse",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(18.dp))
            TextField(
                labelValue = "Name/ID of nurse", onValueChange = {
                    nurseViewModel.updateCurrentSearchName(it)
                }, textFieldValue = currentSearchName
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = {
                    // Check if the input is a Integer
                    if (currentSearchName.toIntOrNull() != null) {
                        // Call to the method to getNurseById
                        remoteApiMessage.getNurseById(currentSearchName.toInt())
                    } else {
                        // Call to the method to findByName
                        remoteApiMessage.findByName(currentSearchName)
                    }
                }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (message) {
                is RemoteApiMessageNurse.Loading -> {
                    Log.d("Loading", "Searching Nurse")
                }

                is RemoteApiMessageNurse.Error -> {
                    Text(
                        text = "No Nurse Found by this name/id",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is RemoteApiMessageNurse.Success -> {
                    ListSearchNurse(message.message, remoteViewModel = remoteApiMessage)
                }
            }
        }
    }
}

