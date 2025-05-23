package com.example.hospitalfrontend.ui.auxiliary.view

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hospitalfrontend.R
import com.example.hospitalfrontend.data.remote.response.RemoteApiMessageBoolean
import com.example.hospitalfrontend.data.remote.viewmodel.DietRemoteViewModel
import com.example.hospitalfrontend.ui.auxiliary.viewmodel.AuxiliaryViewModel
import com.example.hospitalfrontend.ui.diagnosis.view.*
import com.example.hospitalfrontend.utils.LanguageManager

enum class DialogType {
    DIET_TYPE,
    DIET_TEXTURE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    auxiliaryViewModel: AuxiliaryViewModel
) {
    val context = LocalContext.current
    // Get actual language of system
    val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        @Suppress("DEPRECATION")
        context.resources.configuration.locale
    }
    var selectedLanguage by remember { mutableStateOf(currentLocale.language) }

    val customPrimaryColor = Color(0xFFA9C7C7)
    var dietRemoteViewModel: DietRemoteViewModel = viewModel()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }
    var currentDialogType by remember { mutableStateOf<DialogType?>(null) }

    Scaffold(
        containerColor = customPrimaryColor,
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = stringResource(id = R.string.settings_title),
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = customPrimaryColor,
                    scrolledContainerColor = customPrimaryColor
                ),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(customPrimaryColor)
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Create new type diet for the list
                SettingsCard(
                    title = stringResource(id = R.string.setting_diet_type_title),
                    subtitle = stringResource(id = R.string.setting_diet_type_text),
                    icon = Icons.Filled.FilterList,
                    onClick = {
                        dietRemoteViewModel.cleanMessage()
                        currentDialogType = DialogType.DIET_TYPE
                        showInputDialog = true
                    }
                )

                // Create a new texture type diet for the list
                SettingsCard(
                    title = stringResource(id = R.string.setting_diet_texture_title),
                    subtitle = stringResource(id = R.string.setting_diet_texture_text),
                    icon = Icons.Filled.Texture,
                    onClick = {
                        dietRemoteViewModel.cleanMessage()
                        currentDialogType = DialogType.DIET_TEXTURE
                        showInputDialog = true
                    }
                )

                // Language Settings Card
                SettingsCard(
                    title = stringResource(id = R.string.settings_language_title),
                    subtitle = stringResource(id = R.string.settings_language_subtitle),
                    icon = Icons.Filled.Language,
                    onClick = { showLanguageDialog = true }
                )

                // Logout Card
                SettingsCard(
                    title = stringResource(id = R.string.settings_logout_title),
                    subtitle = stringResource(id = R.string.settings_logout_subtitle),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // App Info Section
                AppInfoSection(auxiliaryViewModel)
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = selectedLanguage,
            onLanguageSelected = { language ->
                selectedLanguage = language
                // Use LanguageManager to change language and reload the activity
                LanguageManager.setLanguage(context, language)
                (context as? Activity)?.recreate()
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                auxiliaryViewModel.disconnectAuxiliary()
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showInputDialog && currentDialogType != null) {
        InputDialog(
            dialogType = currentDialogType!!,
            dietRemoteViewModel = dietRemoteViewModel,
            onSave = { inputText ->
                when (currentDialogType) {
                    DialogType.DIET_TYPE -> {
                        dietRemoteViewModel.addDietType(inputText)
                    }

                    DialogType.DIET_TEXTURE -> {
                        dietRemoteViewModel.addDietTexture(inputText)
                    }

                    null -> {}
                }
            },
            onDismiss = {
                showInputDialog = false
                currentDialogType = null
            }
        )
    }
}

@Composable
fun InputDialog(
    dialogType: DialogType,
    dietRemoteViewModel: DietRemoteViewModel,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Get State of dialog response
    val responseState = when (dialogType) {
        DialogType.DIET_TYPE -> dietRemoteViewModel.remoteNewDietType.value
        DialogType.DIET_TEXTURE -> dietRemoteViewModel.remoteNewDietTexture.value
    }

    val (dialogTitle, placeholder, saveButtonText) = when (dialogType) {
        DialogType.DIET_TYPE -> Triple(
            stringResource(id = R.string.setting_diet_type_text),
            stringResource(id = R.string.setting_diet_type_title),
            stringResource(id = R.string.dialog_confirm),
        )

        DialogType.DIET_TEXTURE -> Triple(
            stringResource(id = R.string.setting_diet_texture_text),
            stringResource(id = R.string.setting_diet_texture_title),
            stringResource(id = R.string.dialog_confirm),
        )
    }

    fun isValidInput(text: String): Boolean {
        return text.all { it.isLetter() || it.isWhitespace() } && text.trim().isNotEmpty()
    }

    // Update isLoading state
    LaunchedEffect(responseState) {
        isLoading = responseState is RemoteApiMessageBoolean.Loading && inputText.isNotEmpty()
    }

    AlertDialog(
        onDismissRequest = {
            if (!isLoading && responseState !is RemoteApiMessageBoolean.Success && responseState !is RemoteApiMessageBoolean.Error) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = when (responseState) {
                    is RemoteApiMessageBoolean.Success -> stringResource(id = R.string.dialog_success)
                    is RemoteApiMessageBoolean.Error -> stringResource(id = R.string.dialog_error_title)
                    else -> dialogTitle
                },
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = when (responseState) {
                        is RemoteApiMessageBoolean.Success -> Color(0xFF4CAF50)
                        is RemoteApiMessageBoolean.Error -> Color(0xFFD32F2F)
                        else -> Color(0xFF2C3E50)
                    }
                )
            )
        },
        text = {
            when (responseState) {
                is RemoteApiMessageBoolean.Success -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "\"${inputText.trim()}\" ${stringResource(id = R.string.setting_success)}",
                            style = TextStyle(
                                fontFamily = LatoFontFamily,
                                fontSize = 16.sp,
                                color = Color(0xFF2C3E50)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is RemoteApiMessageBoolean.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${stringResource(id = R.string.settings_error_title)} \"${inputText.trim()}\". ${
                                stringResource(
                                    id = R.string.settings_error_text
                                )
                            }.",
                            style = TextStyle(
                                fontFamily = LatoFontFamily,
                                fontSize = 16.sp,
                                color = Color(0xFF2C3E50)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Column {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { newText ->
                                val filteredText =
                                    newText.filter { it.isLetter() || it.isWhitespace() }
                                inputText = filteredText
                                isError = filteredText.trim().isEmpty()
                            },
                            label = {
                                Text(
                                    text = placeholder,
                                    style = TextStyle(
                                        fontFamily = LatoFontFamily,
                                        fontSize = 14.sp
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = isError,
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFA9C7C7),
                                focusedLabelColor = Color(0xFFA9C7C7),
                                cursorColor = Color(0xFFA9C7C7)
                            )
                        )

                        if (isError) {
                            Text(
                                text = stringResource(id = R.string.setting_diet_input),
                                color = Color(0xFFD32F2F),
                                style = TextStyle(
                                    fontFamily = LatoFontFamily,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (responseState) {
                is RemoteApiMessageBoolean.Success,
                is RemoteApiMessageBoolean.Error -> {
                    Button(
                        onClick = {
                            dietRemoteViewModel.cleanMessage()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA9C7C7)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.dialog_confirm),
                            style = TextStyle(
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                else -> {
                    // Save button
                    Button(
                        onClick = {
                            if (isValidInput(inputText)) {
                                onSave(inputText.trim())
                            } else {
                                isError = true
                            }
                        },
                        enabled = inputText.trim().isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2ECC71),
                            disabledContainerColor = Color(0xFF2ECC71).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = saveButtonText,
                                style = TextStyle(
                                    fontFamily = NunitoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            if (responseState !is RemoteApiMessageBoolean.Success &&
                responseState !is RemoteApiMessageBoolean.Error &&
                !isLoading
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(id = R.string.dialog_cancel),
                        style = TextStyle(
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7F8C8D)
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun SettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val cardColor = if (isDestructive) Color(0xFFFFEBEE) else Color.White
    val iconColor = if (isDestructive) Color(0xFFE74C3C) else Color(0xFF505050)
    val titleColor = if (isDestructive) Color(0xFFD32F2F) else Color(0xFF2C3E50)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                )
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = LatoFontFamily,
                        fontSize = 16.sp,
                        color = Color(0xFF7F8C8D)
                    )
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = Color(0xFF7F8C8D),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppInfoSection(auxiliaryViewModel: AuxiliaryViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            auxiliaryViewModel.auxiliaryState.value?.let {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Auxiliary",
                    tint = Color(0xFFA9C7C7),
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = "${it.name} ${it.surname}",
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontSize = 26.sp,
                        color = Color(0xFF2C3E50)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = stringResource(id = R.string.app_name),
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            )

            Text(
                text = stringResource(id = R.string.settings_app_version),
                style = TextStyle(
                    fontFamily = LatoFontFamily,
                    fontSize = 16.sp,
                    color = Color(0xFF7F8C8D)
                )
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.settings_language_dialog_title),
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
        },
        text = {
            Column {
                LanguageOption(
                    language = "es",
                    label = stringResource(id = R.string.language_spanish),
                    isSelected = currentLanguage == "es",
                    onSelected = { onLanguageSelected("es") }
                )
                LanguageOption(
                    language = "ca",
                    label = stringResource(id = R.string.language_catalan),
                    isSelected = currentLanguage == "ca",
                    onSelected = { onLanguageSelected("ca") }
                )
                LanguageOption(
                    language = "en",
                    label = stringResource(id = R.string.language_english),
                    isSelected = currentLanguage == "en",
                    onSelected = { onLanguageSelected("en") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.dialog_close),
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun LanguageOption(
    language: String,
    label: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFFA9C7C7)
            )
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(
                fontFamily = LatoFontFamily,
                fontSize = 16.sp,
                color = Color(0xFF2C3E50),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFFA9C7C7),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.settings_logout_dialog_title),
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFD32F2F)
                )
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.settings_logout_dialog_message),
                style = TextStyle(
                    fontFamily = LatoFontFamily,
                    fontSize = 16.sp,
                    color = Color(0xFF2C3E50)
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Text(
                    stringResource(id = R.string.settings_logout_confirm),
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(id = R.string.dialog_cancel),
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        containerColor = Color.White
    )
}