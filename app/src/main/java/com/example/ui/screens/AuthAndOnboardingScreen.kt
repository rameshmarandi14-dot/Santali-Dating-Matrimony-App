package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.SantaliConstants
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.GradientActionButton
import com.example.ui.components.OtpVerificationDialog
import com.example.ui.components.SantaliGradientBrush
import com.example.ui.components.SantaliHorizontalGradientBrush

@Composable
fun AuthAndOnboardingScreen(
    authError: String?,
    onClearAuthError: () -> Unit,
    onLoginWithCredentials: (identifier: String, password: String) -> Unit,
    onSignInDemo: () -> Unit,
    onCreateProfile: (
        fullName: String,
        age: Int,
        gender: String,
        parsiClan: String,
        location: String,
        state: String,
        educationProfession: String,
        educationCategory: String,
        photoUri: String?,
        accountIdentifier: String,
        accountPassword: String
    ) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }

    // Login state
    var mobileOrEmail by remember { mutableStateOf("+91 98765 43210") }
    var password by remember { mutableStateOf("santali123") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Register / Create Profile state
    var regIdentifier by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("24") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedClan by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Jharkhand") }
    var education by remember { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    var clanDropdownExpanded by remember { mutableStateOf(false) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SantaliGradientBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            BrandLogoBadge(size = 88.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isRegisterMode) stringResource(R.string.create_your_profile)
                else stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isRegisterMode) stringResource(R.string.create_profile_subtitle)
                else stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.widthIn(max = 520.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (isRegisterMode) 44.dp else 0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (isRegisterMode) {
                            Spacer(modifier = Modifier.height(42.dp))
                        }

                        // Segmented Login / Register switcher
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                val loginMod = if (!isRegisterMode) {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(50))
                                        .background(SantaliHorizontalGradientBrush)
                                } else {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(50))
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = loginMod
                                        .clickable {
                                            isRegisterMode = false
                                            validationError = null
                                            onClearAuthError()
                                        }
                                        .testTag("auth_login_tab")
                                ) {
                                    Text(
                                        text = stringResource(R.string.tab_login),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (!isRegisterMode) Color.White
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val regMod = if (isRegisterMode) {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(50))
                                        .background(SantaliHorizontalGradientBrush)
                                } else {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(50))
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = regMod
                                        .clickable {
                                            isRegisterMode = true
                                            validationError = null
                                            onClearAuthError()
                                        }
                                        .testTag("auth_register_tab")
                                ) {
                                    Text(
                                        text = stringResource(R.string.tab_register),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (isRegisterMode) Color.White
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (!isRegisterMode) {
                            OutlinedTextField(
                                value = mobileOrEmail,
                                onValueChange = {
                                    mobileOrEmail = it
                                    validationError = null
                                    onClearAuthError()
                                },
                                label = { Text(stringResource(R.string.mobile_or_email)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input")
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    validationError = null
                                    onClearAuthError()
                                },
                                label = { Text(stringResource(R.string.password)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                            else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            GradientActionButton(
                                text = stringResource(R.string.login_cta),
                                onClick = {
                                    if (mobileOrEmail.isBlank() || password.isBlank()) {
                                        validationError = "Please enter your mobile/email and password"
                                    } else {
                                        validationError = null
                                        onLoginWithCredentials(mobileOrEmail, password)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_submit_button")
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f))
                                Text(
                                    text = " OR ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = { showOtpDialog = true },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("continue_with_otp_button")
                            ) {
                                Icon(
                                    Icons.Filled.PhoneAndroid,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.continue_with_otp))
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = {
                                    onClearAuthError()
                                    onSignInDemo()
                                },
                                modifier = Modifier.testTag("quick_demo_login_button")
                            ) {
                                Text(
                                    text = stringResource(R.string.quick_demo_login),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        } else {
                            // Registration / Create Your Profile fields
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(stringResource(R.string.full_name)) },
                                placeholder = { Text("e.g., Rohan Soren") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input")
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { input ->
                                    if (input.length <= 2 && input.all { it.isDigit() }) {
                                        ageText = input
                                    }
                                },
                                label = { Text(stringResource(R.string.age)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_age_input")
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.gender),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    SantaliConstants.GENDERS.forEach { gender ->
                                        val isSelected = selectedGender == gender
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.surface
                                            else Color.Transparent,
                                            shadowElevation = if (isSelected) 2.dp else 0.dp,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .padding(3.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { selectedGender = gender }
                                                .testTag("gender_${gender.lowercase()}")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = gender,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.choose_parsi),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { clanDropdownExpanded = true }
                                        .testTag("register_clan_dropdown")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text(
                                            text = selectedClan.ifEmpty { stringResource(R.string.select_clan) },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (selectedClan.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(
                                    expanded = clanDropdownExpanded,
                                    onDismissRequest = { clanDropdownExpanded = false }
                                ) {
                                    SantaliConstants.ALL_PARSI_CLANS.forEach { clan ->
                                        DropdownMenuItem(
                                            text = { Text(clan) },
                                            onClick = {
                                                selectedClan = clan
                                                clanDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = { Text(stringResource(R.string.location)) },
                                placeholder = { Text("e.g., Ranchi, Jharkhand") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_location_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { stateDropdownExpanded = true }
                                        .testTag("register_state_dropdown")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text("State: $selectedState", style = MaterialTheme.typography.bodyMedium)
                                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(
                                    expanded = stateDropdownExpanded,
                                    onDismissRequest = { stateDropdownExpanded = false }
                                ) {
                                    SantaliConstants.STATES.drop(1).forEach { st ->
                                        DropdownMenuItem(
                                            text = { Text(st) },
                                            onClick = {
                                                selectedState = st
                                                stateDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = education,
                                onValueChange = { education = it },
                                label = { Text("Education & Profession") },
                                placeholder = { Text("e.g., B.Tech • Software Engineer") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_education_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regIdentifier,
                                onValueChange = {
                                    regIdentifier = it
                                    validationError = null
                                },
                                label = { Text(stringResource(R.string.mobile_or_email)) },
                                placeholder = { Text("e.g., +91 98765 43210") },
                                leadingIcon = {
                                    Icon(Icons.Filled.PhoneAndroid, contentDescription = null)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_identifier_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    validationError = null
                                },
                                label = { Text("Create Password") },
                                placeholder = { Text("Min 4 characters") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Filled.VisibilityOff
                                            else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            GradientActionButton(
                                text = stringResource(R.string.continue_cta),
                                onClick = {
                                    val age = ageText.toIntOrNull() ?: 0
                                    when {
                                        fullName.isBlank() -> validationError = "Please enter your full name"
                                        age < 18 -> validationError = "You must be 18 or older"
                                        selectedClan.isBlank() -> validationError = "Please choose your Parsi (Clan)"
                                        location.isBlank() -> validationError = "Please enter your city/location"
                                        regIdentifier.isBlank() -> validationError = "Please enter your mobile number or email"
                                        regPassword.length < 4 -> validationError = "Password must be at least 4 characters"
                                        else -> {
                                            validationError = null
                                            onCreateProfile(
                                                fullName,
                                                age,
                                                selectedGender,
                                                selectedClan,
                                                location,
                                                selectedState,
                                                education.ifBlank { "Graduate" },
                                                "Graduate",
                                                selectedPhotoUri?.toString(),
                                                regIdentifier,
                                                regPassword
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_continue_button")
                            )
                        }

                        val displayError = validationError ?: authError
                        if (displayError != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = displayError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Floating Photo Picker Avatar in Register Mode
                if (isRegisterMode) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(4.dp, Color.White),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("register_photo_picker_button")
                    ) {
                        if (selectedPhotoUri != null) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = stringResource(R.string.cd_profile_photo),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = stringResource(R.string.cd_add_photo),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            TextButton(
                onClick = {
                    isRegisterMode = !isRegisterMode
                    validationError = null
                    onClearAuthError()
                },
                modifier = Modifier.testTag("toggle_auth_mode_button")
            ) {
                Text(
                    text = if (isRegisterMode) "Already have an account? Login"
                    else "Don't have an account? Sign Up",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
            }
        }
    }

    if (showOtpDialog) {
        OtpVerificationDialog(
            phoneNumber = mobileOrEmail.ifBlank { "+91 98765 XXXXX" },
            onVerified = {
                showOtpDialog = false
                onSignInDemo()
            },
            onDismiss = { showOtpDialog = false }
        )
    }
}
