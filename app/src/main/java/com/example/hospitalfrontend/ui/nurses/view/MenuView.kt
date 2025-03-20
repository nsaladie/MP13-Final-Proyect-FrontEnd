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
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
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
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            { Text("") }, navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tornar a la pantalla home",
                        tint = Color.Black
                    )
                }
            },
        )
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
                navController = navController
            )
        }
    }
}


@Composable
fun ButtonMenuHome(onScreenSelected: () -> Unit, textButton: String, latoFont: FontFamily, navController: NavController) {
    val customGreen = Color(169, 199, 199)
    Button(
        onClick = { navController.navigate("personalData") },
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


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HospitalFrontEndTheme {
        val navController = rememberNavController()
        MenuScreen(
            navController
        )
    }
}

