package com.example.hospitalfrontend.ui.diagnosis.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.data.remote.viewmodel.DiagnosisRemoteViewModel
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.ui.cure.view.FormSection
import com.example.hospitalfrontend.ui.diagnosis.viewmodel.DiagnosisViewModel
import com.example.hospitalfrontend.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisHistoryScreen(
    navController: NavHostController,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    patientId: Int
) {

    val customPrimaryColor = Color(0xFFA9C7C7)
    var isLoading by remember { mutableStateOf(true) }
    val diagnosisScreenError = remember { mutableStateOf(false) }
    var diagnosisList by remember { mutableStateOf<List<DiagnosisState>>(emptyList()) }

    LaunchedEffect(Unit) {
        diagnosisScreenError.value = false
    }


    LaunchedEffect(patientId) {
        diagnosisRemoteViewModel.getDiagnosisListById(patientId, diagnosisViewModel)
    }

    val apiMessageState = diagnosisRemoteViewModel.remoteApiMessageDiagnosis.value
    LaunchedEffect(apiMessageState) {
        when (apiMessageState) {
            is RemoteApiMessageDiagnosis.SuccessList -> {
                isLoading = false
                diagnosisScreenError.value = false
                diagnosisList = apiMessageState.diagnoses
            }

            is RemoteApiMessageDiagnosis.NotFound -> {
                isLoading = false
                diagnosisScreenError.value = false
            }

            is RemoteApiMessageDiagnosis.Error -> {
                isLoading = false
                diagnosisScreenError.value = true
            }

            is RemoteApiMessageDiagnosis.Loading -> {
                isLoading = true
            }
        }
    }
    Scaffold(containerColor = customPrimaryColor, topBar = {
        TopAppBar(
            title = {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.title_history), style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                )
            }
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    Icons.Filled.Close, contentDescription = "Close", tint = Color.Black
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = customPrimaryColor, scrolledContainerColor = customPrimaryColor
        )
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor),
            contentAlignment = Alignment.Center

        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White, modifier = Modifier.size(50.dp)
                    )
                }

                diagnosisList.isEmpty() -> {
                    NoDataInformation(
                        labelRes = R.string.empty_diagnosis,
                        infoRes = R.string.empty_historial_diagnosis,
                        icon = Icons.Filled.NoteAlt
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        diagnosisList.forEachIndexed { index, diagnosis ->
                            FormSection(
                                title = stringResource(R.string.diagnosis_title_history, diagnosisList.size - index)
                                , icon = Icons.Outlined.DisplaySettings
                            ) {
                                DiagnosisDetailsCard(diagnosis)
                            }
                        }
                    }
                }
            }
        }
    }
}

