package com.example.hospitalfrontend.ui.diagnosis.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.data.remote.viewmodel.DiagnosisRemoteViewModel
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageDiagnosis
import com.example.hospitalfrontend.ui.diagnosis.viewmodel.DiagnosisViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme
import com.example.hospitalfrontend.utils.toDiapersText
import com.example.hospitalfrontend.utils.toOxygenLevelText

val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_medium), Font(R.font.nunito_bold, FontWeight.Bold)
)

val LatoFontFamily = FontFamily(
    Font(R.font.lato_regular), Font(R.font.lato_regular, FontWeight.Bold)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreen(
    navController: NavHostController,
    diagnosisRemoteViewModel: DiagnosisRemoteViewModel,
    diagnosisViewModel: DiagnosisViewModel,
    patientId: Int
) {

    val customPrimaryColor = Color(0xFFA9C7C7)
    var isLoading by remember { mutableStateOf(true) }
    var diagnosisState by remember { mutableStateOf<DiagnosisState?>(null) }
    val diagnosisScreenError = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        diagnosisRemoteViewModel.clearApiMessage()
        diagnosisScreenError.value = false
    }


    LaunchedEffect(patientId) {
        diagnosisRemoteViewModel.getDiagnosisById(patientId, diagnosisViewModel)
    }

    val apiMessageState = diagnosisRemoteViewModel.remoteApiMessageDiagnosis.value
    LaunchedEffect(apiMessageState) {
        when (apiMessageState) {
            is RemoteApiMessageDiagnosis.Success -> {
                isLoading = false
                diagnosisScreenError.value = false
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
    LaunchedEffect(diagnosisViewModel.diagnosisDetail) {
        diagnosisViewModel.diagnosisDetail.collect { newState ->
            diagnosisState = newState
        }
    }


    Scaffold(
        containerColor = customPrimaryColor, topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.diagnosis_title), style = TextStyle(
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
                        diagnosisRemoteViewModel.clearApiMessage()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Filled.Close, contentDescription = "Close", tint = Color.Black
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customPrimaryColor, scrolledContainerColor = customPrimaryColor
                ), actions = {
                    IconButton(onClick = {
                        diagnosisRemoteViewModel.clearApiMessage()
                        navController.navigate("createDiagnosis/$patientId")
                    }) {
                        Icon(
                            Icons.Filled.LocalHospital,
                            contentDescription = "Diagnòstic",
                            tint = Color.Black,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                })
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

                diagnosisState == null -> {
                    NoDataInformation(
                        labelRes = R.string.empty_diagnosis,
                        infoRes = R.string.create_diagnosis,
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
                        DiagnosisDetailsCard(diagnosisState)
                    }
                }
            }
        }
    }
}


@Composable
fun DiagnosisDetailsCard(diagnosisState: DiagnosisState?) {
    val customPrimaryColor = Color(0xFFA9C7C7)
    val customIconColor = Color(0xFF505050)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            diagnosisState?.dependencyLevel?.let {
                DetailItemWithIcon(
                    labelRes = R.string.dependency_level,
                    info = it,
                    icon = Icons.Filled.Accessibility,
                    iconColor = customIconColor,
                )
            }

            diagnosisState?.oxygenLevel?.let {
                OxygenSection(diagnosisState, customPrimaryColor)
            }

            diagnosisState?.diapers?.let { diaper ->
                DiapersSection(diagnosisState, customPrimaryColor)
            }

            diagnosisState?.urinaryCatheter?.let {
                DetailItemWithIcon(
                    labelRes = R.string.urinary,
                    info = it,
                    icon = Icons.Filled.Water,
                    iconColor = customIconColor,
                )
            }

            diagnosisState?.rectalCatheter?.let {
                DetailItemWithIcon(
                    labelRes = R.string.rectal,
                    info = it,
                    icon = Icons.Filled.Medication,
                    iconColor = customIconColor,
                )
            }

            diagnosisState?.nasogastricTube?.let {
                DetailItemWithIcon(
                    labelRes = R.string.nasogastric,
                    info = it,
                    icon = Icons.Filled.HealthAndSafety,
                    iconColor = customIconColor,
                )
            }
        }
    }
}

@Composable
fun DiapersSection(diagnosisState: DiagnosisState?, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(primaryColor.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.bolquer
            ), style = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diagnosisState!!.diapers.toDiapersText(),
                color = when (diagnosisState.diapers) {
                    true -> Color(0xFF1EA01E)
                    false -> Color(0xFFE74C3C)
                },
                fontSize = 18.sp,
                fontFamily = LatoFontFamily
            )
        }

        diagnosisState?.diapers?.let { diaper ->
            if (diaper) {
                DetailItemWithIcon(
                    labelRes = R.string.number_changes,
                    info = diagnosisState.totalChangesDiapers.toString(),
                    icon = Icons.Filled.Replay,
                    iconColor = Color(0xFF505050),
                )
                DetailItemWithIcon(
                    labelRes = R.string.skin_status,
                    info = diagnosisState.detailDescription,
                    icon = Icons.Filled.Healing,
                    iconColor = Color(0xFF505050),
                )
            }
        }
    }
}

@Composable
fun OxygenSection(diagnosisState: DiagnosisState?, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(primaryColor.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.oxygen), style = TextStyle(
                fontFamily = NunitoFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diagnosisState!!.oxygenLevel.toOxygenLevelText(),
                color = when {
                    diagnosisState.oxygenLevel > 0 -> Color(0xFF1EA01E)
                    else -> Color(0xFFE74C3C)
                }, fontSize = 18.sp, fontFamily = LatoFontFamily
            )
        }

        if (diagnosisState?.oxygenLevel != null && diagnosisState.oxygenLevel > 0) {
            DetailItemWithIcon(
                labelRes = R.string.type,
                info = diagnosisState.oxygenLevelDescription,
                icon = Icons.Filled.Science,
                iconColor = Color(0xFF505050),
            )
        }
    }
}

@Composable
fun DetailItemWithIcon(
    @StringRes labelRes: Int,
    info: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF505050),
    infoColor: Color = Color(0xFF7F8C8D)
) {
    val label = stringResource(id = labelRes)
    val infoFontSize = 18.sp
    val labelFontSize = 20.sp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label, style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )
            Text(
                text = info, style = TextStyle(
                    fontFamily = LatoFontFamily, fontSize = infoFontSize, color = Color(0xFF7F8C8D)
                )
            )
        }
    }
}

@Composable
fun NoDataInformation(@StringRes labelRes: Int, @StringRes infoRes: Int, icon: ImageVector) {
    val label = stringResource(id = labelRes)
    val info = stringResource(id = infoRes)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            icon,
            contentDescription = "Information",
            tint = Color.White,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = label, style = TextStyle(
                fontSize = 22.sp,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = info,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = LatoFontFamily,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        )
    }
}

