package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ProfileEntity
import com.example.ui.theme.AcceptGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.OnlineDotGreen
import com.example.ui.theme.RejectRed
import com.example.ui.theme.VerifiedSky

val SantaliGradientBrush = Brush.linearGradient(
    colors = listOf(BrandBlue, BrandViolet, BrandMagenta)
)

val SantaliHorizontalGradientBrush = Brush.horizontalGradient(
    colors = listOf(BrandBlue, BrandViolet, BrandMagenta)
)

fun resolvePortraitDrawable(resName: String): Int {
    return when (resName) {
        "portrait_moni" -> R.drawable.portrait_moni
        "portrait_lila" -> R.drawable.portrait_lila
        "portrait_sita" -> R.drawable.portrait_sita
        "portrait_baha" -> R.drawable.portrait_baha
        "portrait_rani" -> R.drawable.portrait_rani
        "portrait_somai" -> R.drawable.portrait_somai
        "portrait_rohan" -> R.drawable.portrait_rohan
        else -> R.drawable.portrait_moni
    }
}

@Composable
fun BrandLogoBadge(
    size: Dp = 76.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Image(
            painter = painterResource(id = R.drawable.santali_juri_logo),
            contentDescription = stringResource(R.string.cd_brand_logo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun ProfilePhoto(
    photoResName: String,
    photoUri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (!photoUri.isNullOrBlank()) {
        AsyncImage(
            model = Uri.parse(photoUri),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = resolvePortraitDrawable(photoResName)),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp
) {
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = stringResource(R.string.cd_verified_badge),
        tint = VerifiedSky,
        modifier = modifier.size(size)
    )
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(
                brush = if (enabled) SantaliHorizontalGradientBrush
                else Brush.horizontalGradient(listOf(Color.Gray, Color.Gray))
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OnlineAvatar(
    profile: ProfileEntity,
    size: Dp = 64.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(modifier = Modifier.size(size)) {
            ProfilePhoto(
                photoResName = profile.photoResName,
                photoUri = profile.photoUri,
                contentDescription = profile.fullName,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, SantaliGradientBrush, CircleShape)
            )
            if (profile.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(OnlineDotGreen)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${profile.fullName.substringBefore(" ")}, ${profile.age}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BiodataDetailBottomSheet(
    profile: ProfileEntity,
    onDismiss: () -> Unit,
    onAccept: (() -> Unit)? = null,
    onReject: (() -> Unit)? = null,
    onMessage: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(24.dp))
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
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                                startY = 220f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${profile.fullName}, ${profile.age}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            VerifiedBadge(size = 22.dp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "${profile.parsiClan} Parsi (Clan) • ${profile.height}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = profile.location,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = profile.educationProfession,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = profile.aboutMe,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Interests & Values",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profile.interestsList.forEach { interest ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = interest,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (onMessage != null) {
                GradientActionButton(
                    text = "Send Message",
                    onClick = {
                        onDismiss()
                        onMessage()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sheet_send_message_button")
                )
            } else if (onAccept != null && onReject != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onReject()
                        },
                        border = BorderStroke(1.5.dp, RejectRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RejectRed),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("sheet_reject_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reject")
                    }
                    Button(
                        onClick = {
                            onDismiss()
                            onAccept()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AcceptGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("sheet_accept_button")
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Accept")
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCelebrationDialog(
    matchedProfile: ProfileEntity,
    currentUser: ProfileEntity?,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit
) {
    val firstName = matchedProfile.fullName.substringBefore(" ")
    Dialog(
        onDismissRequest = onKeepSwiping,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0D101B), Color(0xFF171226), Color(0xFF0B0E17))
                    )
                )
        ) {
            // Celebratory confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val confettiColors = listOf(
                    BrandBlue,
                    BrandMagenta,
                    BrandViolet,
                    Color(0xFF38BDF8),
                    Color(0xFFFBBF24),
                    Color(0xFF34D399)
                )
                val placements = listOf(
                    Triple(0.12f, 0.16f, 25f),
                    Triple(0.24f, 0.21f, -35f),
                    Triple(0.42f, 0.14f, 15f),
                    Triple(0.64f, 0.18f, -25f),
                    Triple(0.84f, 0.15f, 40f),
                    Triple(0.08f, 0.28f, -20f),
                    Triple(0.91f, 0.27f, 30f),
                    Triple(0.16f, 0.36f, 55f),
                    Triple(0.82f, 0.35f, -45f),
                    Triple(0.28f, 0.29f, 12f),
                    Triple(0.72f, 0.26f, -18f),
                    Triple(0.51f, 0.24f, 32f),
                    Triple(0.10f, 0.56f, -22f),
                    Triple(0.89f, 0.59f, 28f),
                    Triple(0.20f, 0.68f, 44f),
                    Triple(0.79f, 0.67f, -36f)
                )
                placements.forEachIndexed { idx, (px, py, angle) ->
                    val color = confettiColors[idx % confettiColors.size]
                    val cx = size.width * px
                    val cy = size.height * py
                    if (idx % 3 == 0) {
                        drawCircle(
                            color = color.copy(alpha = 0.85f),
                            radius = 5.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    } else {
                        rotate(degrees = angle, pivot = Offset(cx, cy)) {
                            drawRect(
                                color = color.copy(alpha = 0.85f),
                                topLeft = Offset(cx - 7.dp.toPx(), cy - 3.dp.toPx()),
                                size = Size(14.dp.toPx(), 6.dp.toPx())
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                // Top gradient header banner matching reference
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(SantaliHorizontalGradientBrush)
                        .statusBarsPadding()
                        .padding(vertical = 14.dp)
                ) {
                    BrandLogoBadge(size = 64.dp)
                }

                // Floating heart pill row
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandBlue.copy(alpha = 0.85f),
                        shadowElevation = 6.dp
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .size(16.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandMagenta.copy(alpha = 0.85f),
                        shadowElevation = 6.dp
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.its_a_match),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You and $firstName liked each other",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Glowing dual circular portraits
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    if (currentUser != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(134.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(BrandBlue.copy(alpha = 0.55f), Color.Transparent)
                                    )
                                )
                        ) {
                            ProfilePhoto(
                                photoResName = currentUser.photoResName,
                                photoUri = currentUser.photoUri,
                                contentDescription = currentUser.fullName,
                                modifier = Modifier
                                    .size(116.dp)
                                    .clip(CircleShape)
                                    .border(3.5.dp, BrandBlue, CircleShape)
                            )
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(134.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(BrandMagenta.copy(alpha = 0.55f), Color.Transparent)
                                )
                            )
                    ) {
                        ProfilePhoto(
                            photoResName = matchedProfile.photoResName,
                            photoUri = matchedProfile.photoUri,
                            contentDescription = matchedProfile.fullName,
                            modifier = Modifier
                                .size(116.dp)
                                .clip(CircleShape)
                                .border(3.5.dp, BrandMagenta, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$firstName, ${matchedProfile.age} - ${matchedProfile.parsiClan}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    if (matchedProfile.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VerifiedBadge(size = 18.dp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = true))
                Spacer(modifier = Modifier.height(28.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 460.dp)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 28.dp)
                ) {
                    GradientActionButton(
                        text = stringResource(R.string.send_message_johar),
                        onClick = onSendMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_dialog_message_button")
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onKeepSwiping,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("match_dialog_continue_button")
                    ) {
                        Text(
                            text = stringResource(R.string.keep_swiping),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}
