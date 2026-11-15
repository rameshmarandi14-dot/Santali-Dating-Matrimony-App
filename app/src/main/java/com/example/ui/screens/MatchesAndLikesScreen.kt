package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ProfileEntity
import com.example.ui.components.BiodataDetailBottomSheet
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.GradientActionButton
import com.example.ui.components.OnlineAvatar
import com.example.ui.components.ProfilePhoto
import com.example.ui.components.SantaliGradientBrush
import com.example.ui.components.SantaliHorizontalGradientBrush
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.AcceptGreen
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.RejectRed

@Composable
fun MatchesAndLikesScreen(
    matches: List<ProfileEntity>,
    incomingLikes: List<ProfileEntity>,
    allowCalls: Boolean,
    isPremium: Boolean,
    onAcceptLike: (ProfileEntity) -> Unit,
    onRejectLike: (ProfileEntity) -> Unit,
    onOpenChat: (ProfileEntity) -> Unit,
    onStartAudioCall: (ProfileEntity) -> Unit,
    onOpenPremium: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var inspectedProfile by remember { mutableStateOf<ProfileEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SantaliGradientBrush)
            .statusBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            BrandLogoBadge(size = 64.dp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.matches_and_likes),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 720.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 18.dp)
                ) {
                    // Segmented Tab Switcher
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            val matchesMod = if (selectedTab == 0) {
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
                                modifier = matchesMod
                                    .clickable { selectedTab = 0 }
                                    .testTag("matches_tab_button")
                            ) {
                                Text(
                                    text = "Matches (${matches.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedTab == 0) Color.White
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            val likesMod = if (selectedTab == 1) {
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
                                modifier = likesMod
                                    .clickable { selectedTab = 1 }
                                    .testTag("likes_you_tab_button")
                            ) {
                                Text(
                                    text = "Likes You (${incomingLikes.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedTab == 1) Color.White
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        if (matches.isEmpty()) {
                            EmptyMatchesState(
                                title = "No Matches Yet",
                                subtitle = "Accept profiles you like on Home or check the Likes You tab to start connecting!"
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Text(
                                        text = stringResource(R.string.new_matches),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(matches, key = { "online_${it.id}" }) { match ->
                                            OnlineAvatar(
                                                profile = match,
                                                onClick = { onOpenChat(match) }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                items(matches, key = { it.id }) { match ->
                                    MatchCardItem(
                                        profile = match,
                                        allowCalls = allowCalls,
                                        onCardClick = { inspectedProfile = match },
                                        onMessageClick = { onOpenChat(match) },
                                        onCallClick = { onStartAudioCall(match) }
                                    )
                                }
                            }
                        }
                    } else {
                        if (incomingLikes.isEmpty()) {
                            EmptyMatchesState(
                                title = "You're All Caught Up",
                                subtitle = "New incoming interests from the Santali community will appear here."
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (!isPremium) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = BrandMagenta.copy(alpha = 0.10f),
                                        border = BorderStroke(1.dp, BrandMagenta.copy(alpha = 0.35f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable(onClick = onOpenPremium)
                                            .testTag("likes_upgrade_premium_banner")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.WorkspacePremium,
                                                contentDescription = null,
                                                tint = BrandMagenta,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Get Premium for unlimited likes & priority matches",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "Upgrade",
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = BrandMagenta
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.likes_you_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = 156.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 24.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(incomingLikes, key = { it.id }) { profile ->
                                        IncomingLikeGridCard(
                                            profile = profile,
                                            onCardClick = { inspectedProfile = profile },
                                            onAccept = { onAcceptLike(profile) },
                                            onReject = { onRejectLike(profile) }
                                        )
                                    }
                                }
                            }
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
            onMessage = if (selectedTab == 0) {
                { onOpenChat(profile) }
            } else null,
            onAccept = if (selectedTab == 1) {
                { onAcceptLike(profile) }
            } else null,
            onReject = if (selectedTab == 1) {
                { onRejectLike(profile) }
            } else null
        )
    }
}

@Composable
private fun MatchCardItem(
    profile: ProfileEntity,
    allowCalls: Boolean,
    onCardClick: () -> Unit,
    onMessageClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onCardClick)
            .testTag("match_card_${profile.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            ProfilePhoto(
                photoResName = profile.photoResName,
                photoUri = profile.photoUri,
                contentDescription = profile.fullName,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${profile.fullName}, ${profile.age}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (profile.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge(size = 18.dp)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${profile.parsiClan} Clan • ${profile.location.substringBefore(",")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GradientActionButton(
                        text = stringResource(R.string.message_cta),
                        height = 38.dp,
                        onClick = onMessageClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("match_message_button_${profile.id}")
                    )
                    IconButton(
                        onClick = onMessageClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = stringResource(R.string.cd_chat),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (allowCalls) {
                        IconButton(
                            onClick = onCallClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("match_call_button_${profile.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = stringResource(R.string.cd_audio_call),
                                tint = BrandMagenta
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomingLikeGridCard(
    profile: ProfileEntity,
    onCardClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onCardClick)
            .testTag("like_card_${profile.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            ProfilePhoto(
                photoResName = profile.photoResName,
                photoUri = profile.photoUri,
                contentDescription = profile.fullName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${profile.fullName.substringBefore(" ")}, ${profile.age} • ${profile.parsiClan}",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedBadge(size = 15.dp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = AcceptGreen,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onAccept)
                            .testTag("like_accept_${profile.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = stringResource(R.string.accept),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.accept),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = RejectRed,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onReject)
                            .testTag("like_reject_${profile.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = stringResource(R.string.reject),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.reject),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMatchesState(title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
