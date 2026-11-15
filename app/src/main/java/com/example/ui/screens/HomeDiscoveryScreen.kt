package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AppPreferences
import com.example.data.model.ProfileEntity
import com.example.ui.components.BiodataDetailBottomSheet
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.GradientActionButton
import com.example.ui.components.ProfilePhoto
import com.example.ui.components.SantaliGradientBrush
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AcceptGreen
import com.example.ui.theme.RejectRed
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeDiscoveryScreen(
    currentUser: ProfileEntity?,
    deck: List<ProfileEntity>,
    preferences: AppPreferences,
    canRewind: Boolean,
    onAccept: (ProfileEntity) -> Unit,
    onReject: (ProfileEntity) -> Unit,
    onRewind: () -> Unit,
    onResetDeck: () -> Unit,
    onOpenFilters: () -> Unit,
    onOpenPremium: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    var inspectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    val topProfile = deck.firstOrNull()

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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Brand Bar matching mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .padding(top = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .align(Alignment.Center)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("home_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = stringResource(R.string.nav_profile),
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onOpenPremium,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("home_premium_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = stringResource(R.string.upgrade_to_premium),
                                    tint = Color(0xFFFDE047)
                                )
                            }
                            IconButton(
                                onClick = onOpenFilters,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("home_filter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = stringResource(R.string.cd_filter),
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = onNavigateToChat,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("home_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = stringResource(R.string.cd_chat),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                BrandLogoBadge(
                    size = 64.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Filter Chip Strip
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onOpenFilters)
                    .testTag("home_active_filter_chip")
            ) {
                val clanFilterLabel = if (preferences.excludeMyClan && currentUser != null) {
                    "Excluding ${currentUser.parsiClan} Clan"
                } else {
                    "All Clans"
                }
                Text(
                    text = "$clanFilterLabel • ${preferences.minAge}–${preferences.maxAge} yrs • ${preferences.selectedState}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (topProfile != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                ) {
                    // Subtle next card preview behind
                    deck.getOrNull(1)?.let { nextProfile ->
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }

                    SwipeableDiscoveryCard(
                        profile = topProfile,
                        onSwipedAccept = { onAccept(topProfile) },
                        onSwipedReject = { onReject(topProfile) },
                        onCardClick = { inspectedProfile = topProfile }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tactile Action Row (Rewind, Accept, Reject)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 460.dp)
                        .padding(bottom = 8.dp)
                ) {
                    // Rewind / Previous Profile
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = if (canRewind) 0.25f else 0.12f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable(enabled = canRewind, onClick = onRewind)
                            .testTag("home_rewind_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = stringResource(R.string.rewind),
                                tint = Color.White.copy(alpha = if (canRewind) 1f else 0.45f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Reject (Red)
                    Surface(
                        shape = CircleShape,
                        color = RejectRed,
                        shadowElevation = 8.dp,
                        border = BorderStroke(3.dp, Color.White.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable { onReject(topProfile) }
                            .testTag("home_reject_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.reject),
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = stringResource(R.string.reject),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // Accept (Green)
                    Surface(
                        shape = CircleShape,
                        color = AcceptGreen,
                        shadowElevation = 8.dp,
                        border = BorderStroke(3.dp, Color.White.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable { onAccept(topProfile) }
                            .testTag("home_accept_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.accept),
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = stringResource(R.string.accept),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    // View Details
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { inspectedProfile = topProfile }
                            .testTag("home_details_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "View biodata details",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            } else {
                // Empty Discovery State
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .widthIn(max = 480.dp)
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No more profiles match your current filters",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try widening your Parsi (Clan), age range, or state preferences—or refresh previously passed profiles.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GradientActionButton(
                            text = "Adjust Filters",
                            onClick = onOpenFilters,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("empty_adjust_filters_button")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onResetDeck,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("empty_reset_deck_button")
                        ) {
                            Text("Review Passed Profiles Again")
                        }
                    }
                }
            }
        }
    }

    inspectedProfile?.let { profile ->
        BiodataDetailBottomSheet(
            profile = profile,
            onDismiss = { inspectedProfile = null },
            onAccept = { onAccept(profile) },
            onReject = { onReject(profile) }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SwipeableDiscoveryCard(
    profile: ProfileEntity,
    onSwipedAccept: () -> Unit,
    onSwipedReject: () -> Unit,
    onCardClick: () -> Unit
) {
    val offsetX = remember(profile.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .rotate((offsetX.value / 45f).coerceIn(-12f, 12f))
            .pointerInput(profile.id) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > 220f -> {
                                    offsetX.animateTo(1100f)
                                    onSwipedAccept()
                                }
                                offsetX.value < -220f -> {
                                    offsetX.animateTo(-1100f)
                                    onSwipedReject()
                                }
                                else -> offsetX.animateTo(0f, spring())
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch { offsetX.animateTo(0f, spring()) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                    }
                )
            }
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onCardClick)
            .testTag("discovery_profile_card_${profile.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
            ) {
                ProfilePhoto(
                    photoResName = profile.photoResName,
                    photoUri = profile.photoUri,
                    contentDescription = profile.fullName,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)),
                                startY = 250f
                            )
                        )
                )

                // Parsi (Clan) Top Tag
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "${profile.parsiClan} Clan",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Swipe decision stamp
                if (offsetX.value > 80f) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AcceptGreen.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "ACCEPT",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                } else if (offsetX.value < -80f) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RejectRed.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "PASS",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${profile.fullName}, ${profile.age}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            VerifiedBadge(size = 20.dp)
                        }
                    }
                    Text(
                        text = "${profile.location} • ${profile.educationProfession}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.aboutMe,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Interests",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.interestsList.take(4).forEach { interest ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
