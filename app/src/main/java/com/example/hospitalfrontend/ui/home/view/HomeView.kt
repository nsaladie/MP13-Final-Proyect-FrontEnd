package com.example.hospitalfrontend.ui.home.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.domain.model.facility.RoomDTO
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientSharedViewModel
import com.example.hospitalfrontend.ui.patients.viewmodel.PatientViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    patientViewModel: PatientViewModel,
    isError: MutableState<Boolean>,
    sharedViewModel: PatientSharedViewModel
) {
    val rooms by patientViewModel.rooms.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    val register by patientViewModel.registers.collectAsState()
    var idsAsignados = rooms.mapNotNull { it.patient?.historialNumber }

    LaunchedEffect(rooms) {
        if (rooms.isNotEmpty()) {
            isLoading = false
            sharedViewModel.updateIdsFromRooms(rooms)
        }
    }


    LaunchedEffect(idsAsignados) {
        idsAsignados = rooms.mapNotNull { it.patient?.historialNumber }
        sharedViewModel.setIdsAsignados(idsAsignados)
    }

    if (isError.value) {
        AlertDialog(onDismissRequest = { isError.value = false }, confirmButton = {
            TextButton(onClick = {
                isError.value = false
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }) {
                Text(text = stringResource(id = R.string.dialog_ok))
            }
        }, title = {
            Text(
                text = stringResource(id = R.string.error_list_title), color = Color.Red
            )
        }, text = { Text(text = stringResource(id = R.string.error_list_text)) })
    }

    val nunitoFont = FontFamily(Font(R.font.nunito_bold))
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.home_title), style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = nunitoFont,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                    )
                },
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = rooms) { room ->
                            RoomListItem(room, navController, idsAsignados)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomListItem(
    room: RoomDTO, navController: NavController, idsAsignados: List<Int>

) {
    val latoFont = FontFamily(Font(R.font.lato_regular))
    // Specific colors as requested
    val cardColor = if (room.patient != null) Color(169, 199, 199) else Color(200, 200, 200)
    var showObservation by remember { mutableStateOf(false) }

    // Format date to dd/mm/yyyy if patient exists
    val formattedDate = room.assignmentDate?.let {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(it)
        } catch (e: Exception) {
            e.toString()
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp, shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (room.patient != null) {
                    navController.navigate("menu/${room.patient.historialNumber}")
                } else {
                    navController.navigate("assignPatient/${room.room?.roomId}")
                }
            }, colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(id = R.string.num_hab)}: ${room.room?.roomNumber}",
                    style = TextStyle(
                        fontFamily = latoFont, fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (room.patient != null) {
                    Text(
                        text = "${stringResource(id = R.string.name)}: ${room.patient.name} ${room.patient.surname}",
                        style = TextStyle(
                            fontFamily = latoFont, fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stringResource(id = R.string.date_entry)}: $formattedDate",
                        style = TextStyle(
                            fontFamily = latoFont, fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val unavailable = stringResource(id = R.string.obs_unavailable)
                    val text =
                        "${stringResource(id = R.string.observation)}: ${room.lastObservation ?: unavailable}"

                    Text(
                        text = text, style = TextStyle(
                            fontFamily = latoFont, fontSize = 18.sp, fontWeight = FontWeight.Medium
                        )
                    )

                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.hab_empty),
                        color = Color.Red,
                        style = TextStyle(
                            fontFamily = latoFont, fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

