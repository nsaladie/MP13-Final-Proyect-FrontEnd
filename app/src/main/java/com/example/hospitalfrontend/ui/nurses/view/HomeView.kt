package com.example.hospitalfrontend.ui.nurses.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.model.RoomState
import com.example.hospitalfrontend.ui.nurses.viewmodels.PatientViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
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
                        popUpTo("home") { inclusive = true }
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
    val backgroundColor = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LLISTAT D'HABITACIONS",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = nunitoFont,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = rooms) { room ->
                        RoomListItem(room, navController)
                    }
                }
            }
        }
    }
}

// Function to format date string to dd/MM/yyyy
fun formatDate(dateString: String): String {
    // Try various date formats that might be encountered
    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss", // ISO format
        "EEE MMM dd HH:mm:ss zzz yyyy", // Wed Mar 19 00:00:00 GMT 2025 format
        "EEE MMM dd HH:mm:ss z yyyy", // Simpler variant
        "yyyy-MM-dd",  // Simple date format
        "dd/MM/yyyy", // Already in target format
        "MM/dd/yyyy" // US format
    )

    for (format in formats) {
        try {
            val inputFormat = SimpleDateFormat(format, Locale.ENGLISH)
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: ParseException) {
            // Try next format
            continue
        }
    }

    // If no format matches, try to extract date using regex for format like "Wed Mar 19 00:00:00 GMT 2025"
    try {
        val pattern = Pattern.compile("\\w+ (\\w+) (\\d+).*?(\\d{4})")
        val matcher = pattern.matcher(dateString)

        if (matcher.find()) {
            val month = matcher.group(1)
            val day = matcher.group(2)
            val year = matcher.group(3)

            // Convert month name to number
            val monthMap = mapOf(
                "Jan" to "01", "Feb" to "02", "Mar" to "03", "Apr" to "04",
                "May" to "05", "Jun" to "06", "Jul" to "07", "Aug" to "08",
                "Sep" to "09", "Oct" to "10", "Nov" to "11", "Dec" to "12"
            )

            val monthNumber = monthMap[month] ?: "01"
            val dayFormatted = day.padStart(2, '0')

            return "$dayFormatted/$monthNumber/$year"
        }
    } catch (e: Exception) {
        // If regex fails, return original
    }

    // If all methods fail, return the original string
    return dateString
}

@Composable
fun RoomListItem(room: RoomState, navController: NavController) {
    val latoFont = FontFamily(Font(R.font.lato_regular))

    // Specific colors as requested
    val cardColor = if (room.patient != null) Color(169, 199, 199) else Color(200, 200, 200)

    // Format date to dd/mm/yyyy if patient exists
    val formattedDate = room.patient?.let {
        formatDate(it.dateEntry.toString())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                room.patient?.let { patient ->
                    navController.navigate("menu/${patient.historialNumber}")
                }
            },
        colors = CardDefaults.cardColors(containerColor = cardColor)
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
                    text = "Número d'habitació: ${room.roomNumber}",
                    style = TextStyle(
                        fontFamily = latoFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (room.patient != null) {
                    Text(
                        text = "Nom: ${room.patient.name} ${room.patient.surname}",
                        style = TextStyle(
                            fontFamily = latoFont,
                            fontSize = 14.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Data d'ingrés: $formattedDate",
                        style = TextStyle(
                            fontFamily = latoFont,
                            fontSize = 14.sp
                        )
                    )
                } else {
                    Text(
                        text = "Habitació buida",
                        color = Color.Red,
                        style = TextStyle(
                            fontFamily = latoFont,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}