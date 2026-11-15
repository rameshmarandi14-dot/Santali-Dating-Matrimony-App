package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ProfileEntity
import com.example.data.model.SantaliConstants
import com.example.data.model.VerificationRecordEntity
import com.example.ui.components.BrandLogoBadge
import com.example.ui.components.GradientActionButton
import com.example.ui.components.SantaliGradientBrush
import com.example.ui.components.SantaliHorizontalGradientBrush
import com.example.ui.theme.AcceptGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandMagenta
import com.example.ui.theme.BrandViolet
import com.example.ui.theme.VerifiedSky
import java.io.File

@Composable
fun IdVerificationScreen(
    currentUser: ProfileEntity?,
    verificationRecord: VerificationRecordEntity?,
    onBack: () -> Unit,
    onSubmitVerification: (
        documentType: String,
        frontImageUri: String,
        backImageUri: String,
        selfieUri: String
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    var selectedDocType by remember(verificationRecord) {
        mutableStateOf(verificationRecord?.documentType ?: SantaliConstants.GOV_ID_TYPES.first())
    }
    var secondaryDropdownExpanded by remember { mutableStateOf(false) }

    var frontUri by remember(verificationRecord) {
        mutableStateOf(verificationRecord?.frontImageUri?.let { Uri.parse(it) })
    }
    var backUri by remember(verificationRecord) {
        mutableStateOf(verificationRecord?.backImageUri?.let { Uri.parse(it) })
    }
    var selfieUri by remember(verificationRecord) {
        mutableStateOf(verificationRecord?.selfieUri?.let { Uri.parse(it) })
    }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var cameraNotice by remember { mutableStateOf<String?>(null) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    val frontPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            frontUri = uri
            validationMessage = null
        }
    }

    val backPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            backUri = uri
            validationMessage = null
        }
    }

    val selfieFallbackPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selfieUri = uri
            cameraNotice = null
            validationMessage = null
        }
    }

    val takeSelfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            selfieUri = pendingCameraUri
            cameraNotice = null
            validationMessage = null
        }
    }

    fun launchCameraCapture() {
        val outputUri = createSelfieTempUri(context)
        pendingCameraUri = outputUri
        try {
            takeSelfieLauncher.launch(outputUri)
        } catch (e: Exception) {
            cameraNotice = "Camera app unavailable on this device. You can select a selfie photo below."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraCapture()
        } else {
            cameraNotice = "Camera permission denied. Allow camera access or select a clear selfie photo."
        }
    }

    val isAlreadyVerified = currentUser?.isVerified == true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient Top Banner matching IMG_20261115_182716_5cb1a6cd.jpg
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(SantaliHorizontalGradientBrush)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(48.dp)
                                .testTag("id_verification_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = Color.White
                            )
                        }
                        BrandLogoBadge(
                            size = 54.dp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.id_verification_title),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Main Card with overlapping Verified Seal
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 34.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = if (isAlreadyVerified) "Verified Santali Member"
                            else stringResource(R.string.get_verified_badge),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAlreadyVerified) {
                                "Your Blue Tick badge is active (${verificationRecord?.documentType ?: selectedDocType})."
                            } else {
                                stringResource(R.string.get_verified_subtitle)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        if (isAlreadyVerified) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = VerifiedSky.copy(alpha = 0.14f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = VerifiedSky,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Blue Tick Active",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = VerifiedSky
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Step 1: Select Government ID
                        Text(
                            text = stringResource(R.string.step_select_gov_id),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val primaryOptions = listOf("Aadhaar Card", "PAN Card")
                            primaryOptions.forEach { docType ->
                                val selected = selectedDocType == docType
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) BrandBlue.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (selected) 1.5.dp else 1.dp,
                                        color = if (selected) BrandBlue else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable { selectedDocType = docType }
                                        .testTag("gov_id_${docType.lowercase().replace(" ", "_")}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    ) {
                                        Text(
                                            text = docType,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) BrandBlue
                                            else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Additional ID options dropdown (Voter ID / Driving License)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { secondaryDropdownExpanded = true },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .testTag("gov_id_more_dropdown")
                            ) {
                                Text(
                                    text = if (selectedDocType !in listOf("Aadhaar Card", "PAN Card")) {
                                        "Selected: $selectedDocType"
                                    } else {
                                        "Other ID (Voter ID / Driving License)"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = secondaryDropdownExpanded,
                                onDismissRequest = { secondaryDropdownExpanded = false }
                            ) {
                                SantaliConstants.GOV_ID_TYPES.forEach { docType ->
                                    DropdownMenuItem(
                                        text = { Text(docType) },
                                        onClick = {
                                            selectedDocType = docType
                                            secondaryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 2: Upload ID Photo
                        Text(
                            text = stringResource(R.string.step_upload_id_photo),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DashedIdUploadBox(
                                label = stringResource(R.string.front_side),
                                imageUri = frontUri,
                                onClick = {
                                    frontPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_id_front_box")
                            )
                            DashedIdUploadBox(
                                label = stringResource(R.string.back_side),
                                imageUri = backUri,
                                onClick = {
                                    backPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_id_back_box")
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Step 3: Take a Live Selfie
                        Text(
                            text = stringResource(R.string.step_take_live_selfie),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            BrandBlue.copy(alpha = 0.14f),
                                            BrandMagenta.copy(alpha = 0.12f)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                FaceScanReticle(selfieUri = selfieUri)

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (selfieUri != null) "Live selfie captured!"
                                    else stringResource(R.string.take_live_selfie_hint),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (selfieUri != null) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (selfieUri != null) AcceptGreen
                                    else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        val hasCam = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (hasCam) {
                                            launchCameraCapture()
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("open_camera_selfie_button")
                                ) {
                                    Text(
                                        text = if (selfieUri != null) "Retake Selfie"
                                        else stringResource(R.string.open_camera),
                                        color = BrandBlue,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }

                                if (cameraNotice != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = cameraNotice!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    TextButton(
                                        onClick = {
                                            selfieFallbackPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.testTag("selfie_gallery_fallback_button")
                                    ) {
                                        Text("Select Selfie Photo Instead")
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating Verified Seal Badge at top of card
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp)
                            .clip(CircleShape)
                            .background(SantaliGradientBrush)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Encrypted Privacy Notice
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.id_encrypted_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (validationMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            GradientActionButton(
                text = if (isAlreadyVerified) "Update Verification"
                else stringResource(R.string.submit_for_verification),
                onClick = {
                    val f = frontUri?.toString()
                    val b = backUri?.toString()
                    val s = selfieUri?.toString()
                    when {
                        f.isNullOrBlank() || b.isNullOrBlank() ->
                            validationMessage = "Please upload both Front Side and Back Side ID photos."
                        s.isNullOrBlank() ->
                            validationMessage = "Please take a live selfie to complete verification."
                        else -> {
                            validationMessage = null
                            onSubmitVerification(selectedDocType, f, b, s)
                            onBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
                    .testTag("submit_verification_button")
            )
        }
    }
}

@Composable
private fun DashedIdUploadBox(
    label: String,
    imageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val outlineColor = if (imageUri != null) AcceptGreen else MaterialTheme.colorScheme.outline

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = outlineColor,
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = if (imageUri != null) null
                    else PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                )
            )
        }

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Surface(
                shape = CircleShape,
                color = AcceptGreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FaceScanReticle(selfieUri: Uri?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(116.dp)
    ) {
        if (selfieUri != null) {
            AsyncImage(
                model = selfieUri,
                contentDescription = "Captured live selfie",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
            )
        } else {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(92.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }

        // Corner brackets + cyan scan line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bracketLen = 18.dp.toPx()
            val stroke = 2.5.dp.toPx()
            val bracketColor = if (selfieUri != null) AcceptGreen else Color.White

            // Top-left
            drawLine(bracketColor, Offset(0f, bracketLen), Offset(0f, 0f), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), stroke, StrokeCap.Round)
            // Top-right
            drawLine(bracketColor, Offset(size.width - bracketLen, 0f), Offset(size.width, 0f), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, bracketLen), stroke, StrokeCap.Round)
            // Bottom-left
            drawLine(bracketColor, Offset(0f, size.height - bracketLen), Offset(0f, size.height), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(0f, size.height), Offset(bracketLen, size.height), stroke, StrokeCap.Round)
            // Bottom-right
            drawLine(bracketColor, Offset(size.width - bracketLen, size.height), Offset(size.width, size.height), stroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(size.width, size.height - bracketLen), Offset(size.width, size.height), stroke, StrokeCap.Round)

            if (selfieUri == null) {
                val midY = size.height / 2f
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFF38BDF8), Color.Transparent)
                    ),
                    start = Offset(8.dp.toPx(), midY),
                    end = Offset(size.width - 8.dp.toPx(), midY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun createSelfieTempUri(context: Context): Uri {
    val file = File(context.cacheDir, "live_selfie_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
