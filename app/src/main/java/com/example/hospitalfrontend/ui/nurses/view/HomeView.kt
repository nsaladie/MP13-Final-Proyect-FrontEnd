package com.example.hospitalfrontend.ui.nurses.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.network.RemoteViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

@Composable
fun HomeScreen(
    navController: NavController,
) {
    val options = listOf(
        "Dades personals del pacient",
        "Motius/Diagnóstic d'ingrés",
        "Llistat de cures"
    )
    val latoFont = FontFamily(Font(R.font.lato_regular))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MENÚ PACIENT",
            style = TextStyle(
                fontFamily = latoFont,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        options.forEach { option ->
            ButtonMenuHome(
                onScreenSelected = { navController.navigate(option.lowercase()) },
                textButton = option
            )
        }
    }
}

@Composable
fun ButtonMenuHome(onScreenSelected: () -> Unit, textButton: String) {
    val customGreen = Color(169, 199, 199)
    Button(
        onClick = onScreenSelected,
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
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.lato_light)),
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HospitalFrontEndTheme {
        val navController = rememberNavController()
        HomeScreen(
            navController
        )
    }
}

