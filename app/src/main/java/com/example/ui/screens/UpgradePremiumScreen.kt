package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AppPreferences
import com.example.data.model.PremiumPlan
import com.example.data.model.PremiumPlans
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.FilterPillChip
import com.example.ui.components.GradientActionButton
import com.example.ui.components.SantaliHorizontalGradientBrush
import com.example.ui.theme.AcceptGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet

@Composable
fun UpgradePremiumScreen(
    preferences: AppPreferences,
    onBack: () -> Unit,
    onActivatePlan: (planId: String, planLabel: String) -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedPlanId by remember(preferences.premiumPlanId) {
        mutableStateOf(preferences.premiumPlanId ?: "6_months")
    }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("UPI (GPay / PhonePe)") }

    val selectedPlan = remember(selectedPlanId) {
        PremiumPlans.ALL.firstOrNull { it.id == selectedPlanId } ?: PremiumPlans.ALL[1]
    }
    val activePlan = remember(preferences.premiumPlanId) {
        PremiumPlans.ALL.firstOrNull { it.id == preferences.premiumPlanId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BrandBlue,
                        BrandViolet.copy(alpha = 0.72f),
                        BrandMagenta.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
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
                        .testTag("premium_back_button")
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

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.upgrade_to_premium),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            if (preferences.isPremium && activePlan != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.92f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = AcceptGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Active Plan: ${activePlan.durationLabel} Premium",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Tier Cards Row matching reference mockup
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                PremiumPlans.ALL.forEach { plan ->
                    val isSelected = plan.id == selectedPlanId
                    val isPopular = plan.id == "6_months"
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            shadowElevation = if (isSelected || isPopular) 10.dp else 4.dp,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) BrandMagenta
                                else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = if (plan.badgeLabel != null) 12.dp else 22.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { selectedPlanId = plan.id }
                                .testTag("premium_plan_${plan.id}")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = if (isPopular) 18.dp else 14.dp
                                    )
                            ) {
                                Text(
                                    text = plan.durationLabel,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                if (plan.subtitleLabel != null) {
                                    Text(
                                        text = plan.subtitleLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isPopular) BrandMagenta else BrandViolet
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "₹${plan.priceInr}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = if (isSelected) BrandBlue
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    plan.perks.forEach { perk ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) BrandMagenta
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = perk,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (plan.badgeLabel != null) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isPopular) BrandMagenta else BrandViolet,
                                shadowElevation = 4.dp,
                                modifier = Modifier.offset(y = 0.dp)
                            ) {
                                Text(
                                    text = plan.badgeLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Perks Highlights Section matching bottom list in mockup
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp)
                ) {
                    val summaryPerks = listOf(
                        "Unlimited Swipes, Likes & Rewinds",
                        "See Everyone Who Liked Your Profile",
                        "Advanced Parsi / Clan & State Filters",
                        "Boost Profile Visibility in Santali Discovery"
                    )
                    summaryPerks.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SantaliHorizontalGradientBrush)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            val isAlreadyOnSelected = preferences.isPremium && preferences.premiumPlanId == selectedPlan.id
            GradientActionButton(
                text = if (isAlreadyOnSelected) {
                    "Current Active Plan (${selectedPlan.durationLabel})"
                } else {
                    stringResource(R.string.continue_and_pay)
                },
                enabled = !isAlreadyOnSelected,
                onClick = { showCheckoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(bottom = 24.dp)
                    .testTag("premium_continue_pay_button")
            )
        }
    }

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = BrandMagenta,
                    modifier = Modifier.size(34.dp)
                )
            },
            title = {
                Text(
                    text = "Confirm ${selectedPlan.durationLabel} Plan",
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Santali Juri ${selectedPlan.durationLabel}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Instant membership unlock",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "₹${selectedPlan.priceInr}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = BrandBlue
                            )
                        }
                    }

                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("UPI (GPay / PhonePe)", "Debit / Credit Card", "NetBanking").forEach { method ->
                            FilterPillChip(
                                label = method,
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                GradientActionButton(
                    text = "Pay ₹${selectedPlan.priceInr}",
                    height = 48.dp,
                    onClick = {
                        showCheckoutDialog = false
                        onActivatePlan(selectedPlan.id, selectedPlan.durationLabel)
                        onBack()
                    },
                    modifier = Modifier.testTag("confirm_premium_payment_button")
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showCheckoutDialog = false },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
