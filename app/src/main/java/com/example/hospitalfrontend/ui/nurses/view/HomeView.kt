package com.example.hospitalfrontend.ui.nurses.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.RoomState
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    patientViewModel: PatientViewModel,
    isError: MutableState<Boolean>,
) {
    val rooms by patientViewModel.rooms.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(rooms) {
        if (rooms.isNotEmpty()) {
            isLoading = false
        }
    }
    if (isError.value) {
        AlertDialog(
            onDismissRequest = { isError.value = false },
            confirmButton = {
                TextButton(onClick = {
                    isError.value = false
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true } // Opcional: elimina "home" de la pila
                    }
                }) {
                    Text("OK")
                }
            },
            title = { Text(text = "Error: List Room", color = Color.Red) },
            text = { Text(text = "Failing into fetching data of list rooms") }
        )
    }

    val nunitoFont = FontFamily(Font(R.font.nunito_bold))

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "LLISTAT D'HABITACIONS",
            style = TextStyle(fontFamily = nunitoFont, fontSize = 30.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    items(items = rooms) { room ->
                        RoomListItem(room, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun RoomListItem(room: RoomState, navController: NavController) {
    val latoFont = FontFamily(Font(R.font.lato_regular))

    val cardColor = if (room.patient != null) Color(169, 199, 199) else Color(200, 200, 200)
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                room.patient?.let { patient ->
                    navController.navigate("menu/${patient.historialNumber}")
                }
            },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = "Número d'habitació: ${room.roomNumber}",
                    style = TextStyle(fontFamily = latoFont)
                    )
                if (room.patient != null) {
                    Text(text = "Nom: ${room.patient.name} ${room.patient.surname}", style = TextStyle(fontFamily = latoFont))
                    Text(text = "Data d'ingrés: ${room.patient.dateEntry}", style = TextStyle(fontFamily = latoFont))
                } else {
                    Text(text = "Habitació buida", color = Color.Red)
                }
            }
        }
    }
}
