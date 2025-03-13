package com.example.hospitalfrontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.hospitalfrontend.network.RemoteApiMessageListNurse
import com.example.hospitalfrontend.network.RemoteViewModel
import com.example.hospitalfrontend.ui.login.LoginScreenAuxiliary
import com.example.hospitalfrontend.ui.nurses.view.*
import com.example.hospitalfrontend.ui.nurses.viewmodels.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.nurses.viewmodels.NurseViewModel
import com.example.hospitalfrontend.ui.theme.HospitalFrontEndTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HospitalFrontEndTheme {
                MyAppHomePage(
                    nurseViewModel = NurseViewModel(), remoteViewModel = RemoteViewModel(),auxiliaryViewModel = AuxiliaryViewModel()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePage() {
    HospitalFrontEndTheme {
        MyAppHomePage(
            nurseViewModel = NurseViewModel(), remoteViewModel = RemoteViewModel(), auxiliaryViewModel = AuxiliaryViewModel()
        )
    }
}

@Composable
fun MyAppHomePage(
    nurseViewModel: NurseViewModel, remoteViewModel: RemoteViewModel, auxiliaryViewModel: AuxiliaryViewModel
) {
    val remoteApiMessageListNurse = remoteViewModel.remoteApiListMessage.value
    // Set up the NavController for navigation
    val navController = rememberNavController()

    // Observe the login state as a StateFlow
    val loginState by nurseViewModel.loginState.collectAsState()
    val loginStateAux by auxiliaryViewModel.loginState.collectAsState()
    // Determines the initial screen according to the authentication status
    val startDestination = if (loginState.isLogin) "home" else "login"

    LaunchedEffect(loginState.isLogin) {
        if (!loginState.isLogin) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
    LaunchedEffect(loginStateAux.isLogin) {
        if (!loginState.isLogin) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
    NavHost(navController = navController, startDestination = startDestination) {

        composable("create") {
            CreateNursePage(
                navController = navController,
                nurseViewModel = nurseViewModel,
                remoteViewModel = remoteViewModel
            )

        }
        composable("find") {
            FindScreen(
                navController = navController,
                nurseViewModel = nurseViewModel,
                remoteApiMessage = remoteViewModel
            )
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("list") {
            //Variable for the error
            val isError = remember { mutableStateOf(false) }
            //Shows us the answer about the request to the API
            LaunchedEffect(Unit) {
                remoteViewModel.getAllNurses()
            }

            when (remoteApiMessageListNurse) {
                is RemoteApiMessageListNurse.Success -> {
                    nurseViewModel.loadNurses(remoteApiMessageListNurse.message)
                }
                is RemoteApiMessageListNurse.Error -> {
                    Log.d("List Error", "Error")
                    isError.value = true
                }

                is RemoteApiMessageListNurse.Loading -> {
                    Log.d("List", "Loading List")

                }

            }
            ListNurseScreen(
                navController = navController,
                nurseViewModel = nurseViewModel,
                isError = isError,
                remoteViewModel = remoteViewModel
            )
        }
        composable("login") {
            LoginScreenAuxiliary(
                navController = navController,
                auxiliaryViewModel = auxiliaryViewModel,
                remoteViewModel = remoteViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                nurseViewModel = nurseViewModel,
                remoteViewModel = remoteViewModel
            )
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
) {
    val options = listOf("Find", "List", "Profile") // Show Find and List when logged in

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_hospital_app),
            contentDescription = "Logo Hospital"
        )
        Text(
            text = "Hospital Menu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 10.dp)
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
    Button(
        onClick = onScreenSelected, modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(8.dp)
    ) {
        Text(textButton)
    }
}

