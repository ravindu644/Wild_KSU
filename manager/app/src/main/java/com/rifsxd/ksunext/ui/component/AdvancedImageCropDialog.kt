package com.rifsxd.ksunext.ui.component

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.rifsxd.ksunext.ui.util.ImageCropUtils
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedImageCropDialog(
    imageUri: Uri?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
    
    // Simplified transformation states - only scale and position
    var scale by remember { mutableFloatStateOf(prefs.getFloat("background_scale_x", 1.0f)) }
    var offsetX by remember { mutableFloatStateOf(prefs.getFloat("background_pos_x", 0.0f)) }
    var offsetY by remember { mutableFloatStateOf(prefs.getFloat("background_pos_y", 0.0f)) }
    
    val painter = rememberAsyncImagePainter(imageUri)
    val (minScale, maxScale) = ImageCropUtils.getScaleLimits()
    val (minTranslation, maxTranslation) = ImageCropUtils.getTranslationLimits()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Image preview with simplified transformations (zoom and drag only)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // Apply zoom
                            scale = (scale * zoom).coerceIn(minScale, maxScale)
                            // Apply pan/drag
                            offsetX = (offsetX + pan.x).coerceIn(minTranslation, maxTranslation)
                            offsetY = (offsetY + pan.y).coerceIn(minTranslation, maxTranslation)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Background Image Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
                
                // Template overlay showing where UI elements will be
                UITemplateOverlay()
            }
            
            // Simple bottom controls - only Reset and Confirm buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        // Reset to defaults
                        scale = 1.0f
                        offsetX = 0.0f
                        offsetY = 0.0f
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
                
                Button(
                    onClick = {
                        // Save transformations for position_adjust mode
                        ImageCropUtils.saveConstrainedScale(prefs, "background_scale_x", scale)
                        ImageCropUtils.saveConstrainedScale(prefs, "background_scale_y", scale)
                        ImageCropUtils.saveConstrainedTranslation(prefs, "background_pos_x", offsetX)
                        ImageCropUtils.saveConstrainedTranslation(prefs, "background_pos_y", offsetY)
                        // Set rotation to 0 since we removed rotation controls
                        ImageCropUtils.saveConstrainedRotation(prefs, "background_rotation", 0.0f)
                        onSave()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
private fun UITemplateOverlay() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val strokeWidth = 2.dp.toPx()
        val cardColor = Color.White.copy(alpha = 0.4f)
        val outlineColor = Color.White.copy(alpha = 0.6f)
        
        val width = size.width
        val height = size.height
        val padding = 32.dp.toPx()
        val cardHeight = 80.dp.toPx()
        val cardSpacing = 16.dp.toPx()
        
        // Draw template cards to show where UI elements will be positioned
        // Top area - Status cards
        var currentY = padding + 60.dp.toPx() // Account for status bar
        
        // Draw 3 status cards in top area
        for (i in 0..2) {
            val cardY = currentY + (i * (cardHeight + cardSpacing))
            drawRoundRect(
                color = cardColor,
                topLeft = androidx.compose.ui.geometry.Offset(padding, cardY),
                size = androidx.compose.ui.geometry.Size(width - 2 * padding, cardHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
            )
            // Draw outline
            drawRoundRect(
                color = outlineColor,
                topLeft = androidx.compose.ui.geometry.Offset(padding, cardY),
                size = androidx.compose.ui.geometry.Size(width - 2 * padding, cardHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                style = Stroke(strokeWidth)
            )
        }
        
        // Bottom area - Navigation and controls
        val bottomY = height - 200.dp.toPx()
        
        // Bottom navigation area
        drawRoundRect(
            color = cardColor,
            topLeft = androidx.compose.ui.geometry.Offset(padding, bottomY),
            size = androidx.compose.ui.geometry.Size(width - 2 * padding, 120.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
        )
        drawRoundRect(
            color = outlineColor,
            topLeft = androidx.compose.ui.geometry.Offset(padding, bottomY),
            size = androidx.compose.ui.geometry.Size(width - 2 * padding, 120.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
            style = Stroke(strokeWidth)
        )
        
        // Add text label using native canvas
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = Color.White.copy(alpha = 0.8f).toArgb()
                textSize = 14.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            drawText(
                "UI Elements Preview",
                width / 2,
                padding + 30.dp.toPx(),
                paint
            )
        }
    }
}