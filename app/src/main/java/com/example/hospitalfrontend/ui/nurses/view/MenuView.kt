package com.example.hospitalfrontend.ui.nurses.view


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.model.PatientState
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

@Composable
fun MenuScreen(
    navController: NavController,
    patientId: Int
) {
    val options = listOf(
        "Dades personals del pacient",
        "Motius/Diagnóstic d'ingrés",
        "Llistat de cures"
    )
    val nunitoFont = FontFamily(Font(R.font.nunito_bold))
    val latoFont = FontFamily(Font(R.font.lato_light))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 16.dp, top = 5.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = "MENÚ PACIENT",
            style = TextStyle(
                fontFamily = nunitoFont,
                fontSize = 30.sp
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )
        options.forEach { option ->
            ButtonMenuHome(
                onScreenSelected = { navController.navigate(option.lowercase()) },
                textButton = option,
                latoFont = latoFont,
                navController = navController,
                patientId = patientId
            )
        }
    }
}


@Composable
fun ButtonMenuHome(onScreenSelected: () -> Unit, textButton: String, latoFont: FontFamily, navController: NavController, patientId: Int) {
    val customGreen = Color(169, 199, 199)
    Button(
        onClick = { navController.navigate("personalData/$patientId") },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(200.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = customGreen)
    ) {
        Text(
            textButton,
            fontSize = 18.sp,
            color = Color.Black, // Color del texto en negro
            fontFamily = latoFont, // Usamos la fuente Lato
            fontWeight = FontWeight.Bold
        )
    }
}


/*@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HospitalFrontEndTheme {
        val navController = rememberNavController()
        MenuScreen(
            navController
        )
    }
}*/

