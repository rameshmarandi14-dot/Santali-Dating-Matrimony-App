package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppPreferences
import com.example.data.model.ProfileEntity
import com.example.data.model.SantaliConstants
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.FilterPillChip
import com.example.ui.components.GradientActionButton
import com.example.ui.components.ProfilePhoto
import com.example.ui.components.SantaliGradientBrush
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.RejectRed
import com.example.ui.theme.VerifiedSky

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileBiodataScreen(
    currentUser: ProfileEntity?,
    isPremium: Boolean,
    onOpenSettings: () -> Unit,
    onOpenVerification: () -> Unit,
    onOpenPremium: () -> Unit,
    onSaveBiodata: (
        fullName: String,
        age: Int,
        gender: String,
        parsiClan: String,
        location: String,
        state: String,
        height: String,
        educationProfession: String,
        educationCategory: String,
        aboutMe: String,
        interests: List<String>,
        photoUri: String?,
        galleryUris: List<String>
    ) -> Unit
) {
    if (currentUser == null) return

    var fullName by remember(currentUser.id) { mutableStateOf(currentUser.fullName) }
    var ageText by remember(currentUser.id) { mutableStateOf(currentUser.age.toString()) }
    var parsiClan by remember(currentUser.id) { mutableStateOf(currentUser.parsiClan) }
    var height by remember(currentUser.id) { mutableStateOf(currentUser.height) }
    var educationProfession by remember(currentUser.id) { mutableStateOf(currentUser.educationProfession) }
    var educationCategory by remember(currentUser.id) { mutableStateOf(currentUser.educationCategory) }
    var location by remember(currentUser.id) { mutableStateOf(currentUser.location) }
    var state by remember(currentUser.id) { mutableStateOf(currentUser.state) }
    var aboutMe by remember(currentUser.id) { mutableStateOf(currentUser.aboutMe) }
    var interests by remember(currentUser.id) { mutableStateOf(currentUser.interestsList) }
    var photoUri by remember(currentUser.id) { mutableStateOf(currentUser.photoUri) }
    var galleryUris by remember(currentUser.id) { mutableStateOf(currentUser.galleryUris) }

    var clanDropdownExpanded by remember { mutableStateOf(false) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUri = uri.toString()
        }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            galleryUris = (galleryUris + uri.toString()).take(3)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SantaliGradientBrush)
            .statusBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                BrandLogoBadge(size = 64.dp)
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.cd_settings),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.my_profile_biodata),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Avatar + Camera Badge
                    Box(modifier = Modifier.size(108.dp)) {
                        ProfilePhoto(
                            photoResName = currentUser.photoResName,
                            photoUri = photoUri,
                            contentDescription = fullName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(3.dp, SantaliGradientBrush, CircleShape)
                        )
                        Surface(
                            shape = CircleShape,
                            color = BrandBlue,
                            border = BorderStroke(2.dp, Color.White),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable {
                                    avatarPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("profile_avatar_edit_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = stringResource(R.string.cd_add_photo),
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentUser.isVerified) {
                            VerifiedBadge(size = 20.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "$fullName, ${ageText.ifBlank { currentUser.age }}",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Text(
                        text = "$parsiClan Clan • $location",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (currentUser.isVerified) VerifiedSky.copy(alpha = 0.14f)
                        else BrandBlue.copy(alpha = 0.10f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (currentUser.isVerified) VerifiedSky.copy(alpha = 0.5f)
                            else BrandBlue.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenVerification)
                            .testTag("profile_get_verified_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = if (currentUser.isVerified) VerifiedSky else BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentUser.isVerified) "Verified Member • Manage"
                                else "Get Verified Blue Tick",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (currentUser.isVerified) VerifiedSky else BrandBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (currentUser.isVerified) VerifiedSky else BrandBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isPremium) BrandViolet.copy(alpha = 0.14f)
                        else BrandMagenta.copy(alpha = 0.10f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isPremium) BrandViolet.copy(alpha = 0.5f)
                            else BrandMagenta.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenPremium)
                            .testTag("profile_upgrade_premium_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                tint = if (isPremium) BrandViolet else BrandMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPremium) "Santali Juri Premium Active"
                                else stringResource(R.string.upgrade_to_premium),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isPremium) BrandViolet else BrandMagenta
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isPremium) BrandViolet else BrandMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3-slot Photo Gallery Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (index in 0 until 3) {
                            val uriStr = galleryUris.getOrNull(index)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(78.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        galleryPicker.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("profile_gallery_slot_$index")
                            ) {
                                if (uriStr != null) {
                                    AsyncImage(
                                        model = uriStr,
                                        contentDescription = "Gallery photo ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    ProfilePhoto(
                                        photoResName = currentUser.photoResName,
                                        photoUri = photoUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Two-column Form Fields matching mockup
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(stringResource(R.string.full_name)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_name_input")
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = parsiClan,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Parsi (Clan)") },
                                trailingIcon = {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { clanDropdownExpanded = true }
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { clanDropdownExpanded = true }
                                    .testTag("profile_clan_dropdown")
                            )
                            DropdownMenu(
                                expanded = clanDropdownExpanded,
                                onDismissRequest = { clanDropdownExpanded = false }
                            ) {
                                SantaliConstants.ALL_PARSI_CLANS.forEach { clan ->
                                    DropdownMenuItem(
                                        text = { Text(clan) },
                                        onClick = {
                                            parsiClan = clan
                                            clanDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { input ->
                                if (input.length <= 2 && input.all { it.isDigit() }) ageText = input
                            },
                            label = { Text("Age") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .testTag("profile_age_input")
                        )
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .testTag("profile_height_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = educationProfession,
                        onValueChange = { educationProfession = it },
                        label = { Text("Education & Profession") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_education_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(stringResource(R.string.location)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_location_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = aboutMe,
                        onValueChange = { aboutMe = it },
                        label = { Text("About Me") },
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_about_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Interests",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SantaliConstants.INTEREST_OPTIONS.forEach { interest ->
                            val selected = interest in interests
                            FilterPillChip(
                                label = interest,
                                selected = selected,
                                onClick = {
                                    interests = if (selected) interests - interest else interests + interest
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    GradientActionButton(
                        text = stringResource(R.string.save_changes),
                        onClick = {
                            onSaveBiodata(
                                fullName.ifBlank { currentUser.fullName },
                                ageText.toIntOrNull()?.coerceIn(18, 80) ?: currentUser.age,
                                currentUser.gender,
                                parsiClan,
                                location.ifBlank { currentUser.location },
                                state,
                                height,
                                educationProfession,
                                educationCategory,
                                aboutMe,
                                interests,
                                photoUri,
                                galleryUris
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_save_button")
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsPrivacyScreen(
    preferences: AppPreferences,
    currentUser: ProfileEntity?,
    blockedProfiles: List<ProfileEntity>,
    onBack: () -> Unit,
    onEditBiodata: () -> Unit,
    onToggleHideSameClan: (Boolean) -> Unit,
    onTogglePhotoPrivacy: (Boolean) -> Unit,
    onToggleAllowCalls: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onUnblockUser: (String) -> Unit,
    onOpenVerification: () -> Unit,
    onOpenPremium: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    BackHandler(onBack = onBack)

    var showBlockedDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White
                    )
                }
                BrandLogoBadge(
                    size = 64.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.settings_privacy),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.privacy_safety),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsToggleRow(
                        icon = Icons.Filled.Groups,
                        title = "Hide Profile from Same Parsi (Clan)",
                        checked = preferences.hideFromSameClan,
                        onCheckedChange = onToggleHideSameClan,
                        testTag = "settings_hide_same_clan_switch"
                    )
                    SettingsToggleRow(
                        icon = Icons.Filled.VisibilityOff,
                        title = "Photo Privacy (Verified Matches Only)",
                        checked = preferences.photoPrivacyVerifiedOnly,
                        onCheckedChange = onTogglePhotoPrivacy,
                        testTag = "settings_photo_privacy_switch"
                    )
                    SettingsToggleRow(
                        icon = Icons.Filled.Call,
                        title = "Allow Audio & Video Calls",
                        checked = preferences.allowCalls,
                        onCheckedChange = onToggleAllowCalls,
                        testTag = "settings_allow_calls_switch"
                    )
                    SettingsNavigationRow(
                        icon = Icons.Filled.Block,
                        title = "Blocked Users (${blockedProfiles.size})",
                        onClick = { showBlockedDialog = true },
                        testTag = "settings_blocked_users_row"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.VerifiedUser,
                            contentDescription = null,
                            tint = BrandViolet
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.account_verification),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsNavigationRow(
                        icon = Icons.Filled.VerifiedUser,
                        title = if (currentUser?.isVerified == true) "ID & Blue Tick Verified"
                        else "ID Verification & Blue Tick (Get Badge)",
                        onClick = onOpenVerification,
                        testTag = "settings_verification_row"
                    )
                    SettingsNavigationRow(
                        icon = Icons.Filled.WorkspacePremium,
                        title = if (preferences.isPremium) "Santali Juri Premium (Active)"
                        else "Upgrade to Premium",
                        onClick = onOpenPremium,
                        testTag = "settings_premium_row"
                    )
                    SettingsNavigationRow(
                        icon = Icons.Filled.Edit,
                        title = "Edit Parsi & Biodata",
                        onClick = onEditBiodata,
                        testTag = "settings_edit_biodata_row"
                    )
                    SettingsToggleRow(
                        icon = Icons.Filled.Notifications,
                        title = "Notifications",
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = onToggleNotifications,
                        testTag = "settings_notifications_switch"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_logout_button")
                    ) {
                        Text(
                            text = stringResource(R.string.log_out),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = RejectRed),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(48.dp)
                            .testTag("settings_delete_account_button")
                    ) {
                        Text(
                            text = stringResource(R.string.delete_account),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }

    if (showBlockedDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedDialog = false },
            title = { Text("Blocked Users") },
            text = {
                if (blockedProfiles.isEmpty()) {
                    Text("You haven't blocked anyone.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        blockedProfiles.forEach { blocked ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${blocked.fullName} (${blocked.parsiClan})")
                                TextButton(onClick = { onUnblockUser(blocked.id) }) {
                                    Text("Unblock")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockedDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = { Text("This will remove your local profile, matches, and messages and reset the app.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = RejectRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = BrandMagenta),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
