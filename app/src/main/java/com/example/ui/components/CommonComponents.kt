package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Dynamic Canvas-drawn Coat of Arms of Tanah Bumbu / kelurahan logo for high-clarity government branding
@Composable
fun GovernmentLogo(modifier: Modifier = Modifier, logoUrl: String? = null) {
    val context = LocalContext.current
    val appIcon = remember(context) {
        try {
            context.packageManager.getApplicationIcon(context.packageName)
        } catch (e: Exception) {
            null
        }
    }

    if (!logoUrl.isNullOrEmpty()) {
        AsyncImage(
            model = logoUrl,
            contentDescription = "Logo Aplikasi",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else if (appIcon != null) {
        AsyncImage(
            model = appIcon,
            contentDescription = "Logo APK",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    } else {
        val shieldBrush = remember {
            androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
            )
        }
        val borderBrush = remember {
            androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFD700))
            )
        }
        val seaBrush = remember {
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(Color(0xFF039BE5), Color(0xFF01579B))
            )
        }

        Spacer(
            modifier = modifier.drawWithCache {
                val w = size.width
                val h = size.height
                
                // Initialize paths only when size changes
                val shieldPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.1f)
                    lineTo(w * 0.85f, h * 0.1f)
                    lineTo(w * 0.85f, h * 0.65f)
                    quadraticTo(w * 0.85f, h * 0.95f, w * 0.5f, h * 0.98f)
                    quadraticTo(w * 0.15f, h * 0.95f, w * 0.15f, h * 0.65f)
                    close()
                }

                val towerLeft = Path().apply {
                    moveTo(w * 0.44f, h * 0.72f)
                    lineTo(w * 0.46f, h * 0.35f)
                    lineTo(w * 0.5f, h * 0.35f)
                    lineTo(w * 0.5f, h * 0.72f)
                    close()
                }

                val towerRight = Path().apply {
                    moveTo(w * 0.5f, h * 0.35f)
                    lineTo(w * 0.54f, h * 0.35f)
                    lineTo(w * 0.56f, h * 0.72f)
                    lineTo(w * 0.5f, h * 0.72f)
                    close()
                }

                val peakLeft = Path().apply {
                    moveTo(w * 0.46f, h * 0.34f)
                    lineTo(w * 0.5f, h * 0.22f)
                    lineTo(w * 0.5f, h * 0.34f)
                    close()
                }

                val peakRight = Path().apply {
                    moveTo(w * 0.5f, h * 0.22f)
                    lineTo(w * 0.54f, h * 0.34f)
                    lineTo(w * 0.5f, h * 0.34f)
                    close()
                }

                val seaPath = Path().apply {
                    moveTo(w * 0.2f, h * 0.65f)
                    quadraticTo(w * 0.35f, h * 0.62f, w * 0.5f, h * 0.65f)
                    quadraticTo(w * 0.65f, h * 0.68f, w * 0.8f, h * 0.65f)
                    lineTo(w * 0.8f, h * 0.75f)
                    quadraticTo(w * 0.5f, h * 0.8f, w * 0.2f, h * 0.75f)
                    close()
                }

                onDrawBehind {
                    if (w > 0f && h > 0f) {
                        drawPath(path = shieldPath, brush = shieldBrush)
                        drawPath(path = shieldPath, brush = borderBrush, style = Stroke(width = 3.dp.toPx()))

                        drawLine(
                            color = Color.White.copy(alpha = 0.3f),
                            start = Offset(w * 0.2f, h * 0.12f),
                            end = Offset(w * 0.8f, h * 0.12f),
                            strokeWidth = 1.dp.toPx()
                        )

                        drawPath(path = towerLeft, color = Color(0xFFF5F5F5))
                        drawPath(path = towerRight, color = Color(0xFFBDBDBD))
                        drawPath(path = peakLeft, color = Color(0xFFFFD700))
                        drawPath(path = peakRight, color = Color(0xFFB8860B))
                        drawPath(path = seaPath, brush = seaBrush)
                    }
                }
            }
        )
    }
}

// Gorgeous Custom Batulicin Monument landscape illustration done purely using Compose Canvas!
@Composable
fun BatulicinMonumentGraphic(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height

            val waterPath = Path().apply {
                moveTo(0f, h * 0.6f)
                quadraticTo(w * 0.3f, h * 0.55f, w * 0.6f, h * 0.65f)
                quadraticTo(w * 0.8f, h * 0.7f, w, h * 0.6f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            val sandPath = Path().apply {
                moveTo(0f, h * 0.72f)
                quadraticTo(w * 0.4f, h * 0.65f, w * 0.8f, h * 0.78f)
                quadraticTo(w * 0.9f, h * 0.82f, w, h * 0.75f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            val towerPoints = Path().apply {
                moveTo(w * 0.71f, h * 0.62f)
                lineTo(w * 0.73f, h * 0.2f)
                lineTo(w * 0.77f, h * 0.2f)
                lineTo(w * 0.79f, h * 0.62f)
                close()
            }

            val goldCap = Path().apply {
                moveTo(w * 0.73f, h * 0.2f)
                lineTo(w * 0.75f, h * 0.08f)
                lineTo(w * 0.77f, h * 0.2f)
                close()
            }

            val birdPath1 = Path().apply {
                moveTo(w * 0.2f, h * 0.25f)
                quadraticTo(w * 0.23f, h * 0.22f, w * 0.26f, h * 0.25f)
                quadraticTo(w * 0.29f, h * 0.22f, w * 0.32f, h * 0.25f)
            }

            val birdPath2 = Path().apply {
                moveTo(w * 0.45f, h * 0.18f)
                quadraticTo(w * 0.48f, h * 0.15f, w * 0.51f, h * 0.18f)
                quadraticTo(w * 0.54f, h * 0.15f, w * 0.57f, h * 0.18f)
            }

            onDrawBehind {
                if (w > 0f && h > 0f) {
                    drawPath(path = waterPath, color = Color(0xFF039BE5).copy(alpha = 0.8f))
                    drawPath(path = sandPath, color = Color(0xFFFFECB3))

                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f),
                        radius = w * 0.08f,
                        center = Offset(w * 0.75f, h * 0.62f)
                    )

                    drawPath(path = towerPoints, color = Color(0xFFEEEEEE))
                    drawPath(path = towerPoints, color = Color(0xFFBDBDBD), style = Stroke(width = 2f))

                    drawPath(path = goldCap, color = Color(0xFFFFB300))

                    drawRect(
                        color = Color(0xFFD32F2F),
                        topLeft = Offset(w * 0.732f, h * 0.45f),
                        size = androidx.compose.ui.geometry.Size(w * 0.036f, h * 0.02f)
                    )
                    drawRect(
                        color = Color(0xFFFFB300),
                        topLeft = Offset(w * 0.734f, h * 0.32f),
                        size = androidx.compose.ui.geometry.Size(w * 0.032f, h * 0.018f)
                    )

                    drawPath(path = birdPath1, color = Color.White, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))
                    drawPath(path = birdPath2, color = Color.White, style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round))
                }
            }
        }
    )
}

@Composable
fun AssetUploadCard(
    title: String,
    onImageCaptured: (String) -> Unit,
    currentImage: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    
    // Camera Support Logic
    val tempFile = remember { File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg") }
    val tempUri = remember { 
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            onImageCaptured(tempUri.toString())
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImageCaptured(it.toString()) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        onClick = { showDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!currentImage.isNullOrEmpty()) {
                AsyncImage(
                    model = currentImage,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ganti $title", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upload $title", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Tap untuk memilih dari Kamera atau Galeri", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Silakan pilih media untuk mengupload $title") },
            confirmButton = {
                TextButton(onClick = { 
                    cameraLauncher.launch(tempUri)
                    showDialog = false 
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Kamera")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    galleryLauncher.launch("image/*")
                    showDialog = false 
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Galeri")
                    }
                }
            }
        )
    }
}

// Compact and polished Signature Pad Canvas that renders real lines with smooth strokes
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    onSignatureDrawn: (List<List<Offset>>) -> Unit
) {
    val currentPath = remember { mutableStateListOf<Offset>() }
    val allPaths = remember { mutableStateListOf<List<Offset>>() }
    
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentPath.clear()
                            currentPath.add(Offset(event.x, event.y))
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            currentPath.add(Offset(event.x, event.y))
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (currentPath.isNotEmpty()) {
                                allPaths.add(currentPath.toList())
                                currentPath.clear() 
                                onSignatureDrawn(allPaths.toList())
                            }
                            true
                        }
                        else -> false
                    }
                }
                .drawWithCache {
                    val finalizedPath = Path()
                    allPaths.forEach { points ->
                        if (points.isNotEmpty()) {
                            finalizedPath.moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                finalizedPath.lineTo(points[i].x, points[i].y)
                            }
                        }
                    }
                    val stroke = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                    val activeStroke = Stroke(
                        width = 3.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )

                    onDrawBehind {
                        // Draw finalized paths
                        drawPath(
                            path = finalizedPath,
                            color = Color(0xFF0d47a1),
                            style = stroke
                        )
                    }
                }
        ) {
            // Draw active user path efficiently
            if (currentPath.size > 1) {
                // We use drawPoints or create a reusable path if possible, 
                // but for active drawing, creating one path is often necessary.
                // However, we can avoid creating it IF we use drawPoints for the current stroke.
                for (i in 0 until currentPath.size - 1) {
                    drawLine(
                        color = Color(0xFF1565c0),
                        start = currentPath[i],
                        end = currentPath[i+1],
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    currentPath.clear()
                    allPaths.clear()
                    onSignatureDrawn(emptyList())
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Hapus TTD",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (allPaths.isEmpty()) {
            Text(
                text = "Goreskan Tanda Tangan Anda di Sini",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsCategory(title: String, content: @Composable ColumnScope.() -> Unit = {}) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        Column {
            content()
        }
    }
}

@Composable
fun SettingsMenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = Color(0xFF2563EB),
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1.8f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
