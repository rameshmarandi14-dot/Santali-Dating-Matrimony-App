package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppPreferences
import com.example.data.model.SantaliConstants
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterPreferencesBottomSheet(
    preferences: AppPreferences,
    myClan: String,
    onApply: (
        excludeMyClan: Boolean,
        preferredClans: Set<String>,
        minAge: Int,
        maxAge: Int,
        selectedState: String,
        selectedEducation: String,
        verifiedOnly: Boolean
    ) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var excludeMyClan by remember(preferences) { mutableStateOf(preferences.excludeMyClan) }
    var selectedClans by remember(preferences) { mutableStateOf(preferences.preferredClans) }
    var ageRange by remember(preferences) {
        mutableStateOf(preferences.minAge.toFloat()..preferences.maxAge.toFloat())
    }
    var selectedState by remember(preferences) { mutableStateOf(preferences.selectedState) }
    var selectedEducation by remember(preferences) { mutableStateOf(preferences.selectedEducation) }
    var verifiedOnly by remember(preferences) { mutableStateOf(preferences.verifiedOnly) }

    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var eduDropdownExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_preferences),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.preferred_parsi),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allSelected = selectedClans.isEmpty()
                FilterPillChip(
                    label = "All Clans",
                    selected = allSelected,
                    onClick = { selectedClans = emptySet() },
                    modifier = Modifier.testTag("filter_clan_all")
                )
                SantaliConstants.ALL_PARSI_CLANS.forEach { clan ->
                    val isSelected = clan in selectedClans
                    FilterPillChip(
                        label = clan,
                        selected = isSelected,
                        onClick = {
                            selectedClans = if (isSelected) selectedClans - clan else selectedClans + clan
                        },
                        modifier = Modifier.testTag("filter_clan_${clan.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (excludeMyClan) BrandMagenta.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (myClan.isNotBlank()) "Exclude My Clan ($myClan)"
                            else stringResource(R.string.exclude_my_clan),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.exclude_clan_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = excludeMyClan,
                        onCheckedChange = { excludeMyClan = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = BrandMagenta),
                        modifier = Modifier.testTag("filter_exclude_my_clan_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Age Range",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${ageRange.start.toInt()} - ${ageRange.endInclusive.toInt()} yrs",
                    style = MaterialTheme.typography.titleSmall,
                    color = BrandMagenta
                )
            }
            RangeSlider(
                value = ageRange,
                onValueChange = { ageRange = it },
                valueRange = 18f..45f,
                colors = SliderDefaults.colors(
                    thumbColor = BrandMagenta,
                    activeTrackColor = BrandViolet
                ),
                modifier = Modifier.testTag("filter_age_slider")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Location / State",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { stateDropdownExpanded = true }
                        .testTag("filter_state_dropdown")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(selectedState, style = MaterialTheme.typography.bodyMedium)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = stateDropdownExpanded,
                    onDismissRequest = { stateDropdownExpanded = false }
                ) {
                    SantaliConstants.STATES.forEach { stateOption ->
                        DropdownMenuItem(
                            text = { Text(stateOption) },
                            onClick = {
                                selectedState = stateOption
                                stateDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Education & Profession",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { eduDropdownExpanded = true }
                        .testTag("filter_education_dropdown")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Text(selectedEducation, style = MaterialTheme.typography.bodyMedium)
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = eduDropdownExpanded,
                    onDismissRequest = { eduDropdownExpanded = false }
                ) {
                    SantaliConstants.EDUCATION_OPTIONS.forEach { eduOption ->
                        DropdownMenuItem(
                            text = { Text(eduOption) },
                            onClick = {
                                selectedEducation = eduOption
                                eduDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Verified Profiles Only",
                    style = MaterialTheme.typography.titleSmall
                )
                Switch(
                    checked = verifiedOnly,
                    onCheckedChange = { verifiedOnly = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = BrandViolet),
                    modifier = Modifier.testTag("filter_verified_only_switch")
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                GradientActionButton(
                    text = stringResource(R.string.apply_filters),
                    onClick = {
                        onApply(
                            excludeMyClan,
                            selectedClans,
                            ageRange.start.toInt(),
                            ageRange.endInclusive.toInt(),
                            selectedState,
                            selectedEducation,
                            verifiedOnly
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_filters_button")
                )
                TextButton(
                    onClick = {
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("reset_filters_button")
                ) {
                    Text(stringResource(R.string.reset))
                }
            }
        }
    }
}

@Composable
fun FilterPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) BrandViolet.copy(alpha = 0.16f) else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) BrandViolet else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selected) BrandViolet else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun OtpVerificationDialog(
    phoneNumber: String,
    title: String = stringResource(R.string.verify_your_number),
    onVerified: () -> Unit,
    onDismiss: () -> Unit
) {
    var otpCode by remember { mutableStateOf("482915") }
    var countdown by remember { mutableIntStateOf(28) }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L)
            countdown--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                BrandLogoBadge(size = 64.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter the 6-digit verification code for $phoneNumber (demo verification)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until 6) {
                        val char = otpCode.getOrNull(i)?.toString() ?: ""
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (char.isNotEmpty()) MaterialTheme.colorScheme.surfaceVariant
                                    else Color.Transparent
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (char.isEmpty()) BrandBlue else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { input ->
                        if (input.length <= 6 && input.all { it.isDigit() }) {
                            otpCode = input
                        }
                    },
                    label = { Text("6-digit code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_code_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                GradientActionButton(
                    text = stringResource(R.string.verify_and_proceed),
                    enabled = otpCode.length == 6,
                    onClick = onVerified,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_otp_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (countdown > 0) "Resend code in 00:${countdown.toString().padStart(2, '0')}"
                        else "Didn't receive code?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (countdown == 0) {
                        TextButton(onClick = { countdown = 28 }) {
                            Text("Resend")
                        }
                    }
                }
            }
        }
    }
}
