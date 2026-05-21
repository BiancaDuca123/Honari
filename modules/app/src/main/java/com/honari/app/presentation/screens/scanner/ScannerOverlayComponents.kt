package com.honari.app.presentation.screens.scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.PrimaryTeal

private const val OVERLAY_WIDTH_FRACTION = 0.78f
private const val OVERLAY_HEIGHT_FRACTION = 0.18f
private const val OVERLAY_CORNER_RADIUS = 36f
private const val OVERLAY_ALPHA = 0.55f
private const val OVERLAY_BORDER_ALPHA = 0.9f
private const val OVERLAY_BORDER_WIDTH = 5f
private const val MODE_TOGGLE_BACKGROUND_ALPHA = 0.5f
private const val MODE_BUTTON_UNSELECTED_ALPHA = 0.7f
private const val DETECTED_TEXT_BACKGROUND_ALPHA = 0.6f
private const val DETECTED_TEXT_TRACK_ALPHA = 0.4f
private const val DETECTED_TEXT_PREVIEW_LENGTH = 60
private val OVERLAY_HORIZONTAL_PADDING = 24.dp
private val INSTRUCTION_BOTTOM_PADDING = 160.dp
private val MODE_BUTTON_CORNER_RADIUS = 50.dp
private val MODE_BUTTON_HORIZONTAL_PADDING = 16.dp
private val MODE_BUTTON_VERTICAL_PADDING = 8.dp
private val MODE_BUTTON_ICON_SIZE = 16.dp
private val MODE_BUTTON_TEXT_SPACING = 4.dp
private val DETECTED_TEXT_CARD_RADIUS = 12.dp
private val DETECTED_TEXT_CONTENT_PADDING = 12.dp
private val DETECTED_TEXT_SPACING = 4.dp

@Composable
internal fun ScannerOverlay(
    instruction: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.Black.copy(alpha = OVERLAY_ALPHA))

            val overlayWidth = size.width * OVERLAY_WIDTH_FRACTION
            val overlayHeight = size.height * OVERLAY_HEIGHT_FRACTION
            val left = (size.width - overlayWidth) / 2f
            val top = (size.height - overlayHeight) / 2f
            val rect = Rect(left, top, left + overlayWidth, top + overlayHeight)
            val radius = OVERLAY_CORNER_RADIUS

            drawIntoCanvas { canvas ->
                val clearPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    xfermode = android.graphics.PorterDuffXfermode(
                        android.graphics.PorterDuff.Mode.CLEAR,
                    )
                }
                canvas.nativeCanvas.drawRoundRect(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom,
                    radius,
                    radius,
                    clearPaint,
                )
            }

            drawRoundRect(
                color = Color.White.copy(alpha = OVERLAY_BORDER_ALPHA),
                topLeft = Offset(rect.left, rect.top),
                size = androidx.compose.ui.geometry.Size(rect.width, rect.height),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = OVERLAY_BORDER_WIDTH),
                blendMode = BlendMode.SrcOver,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = OVERLAY_HORIZONTAL_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = instruction,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = INSTRUCTION_BOTTOM_PADDING),
            )
        }
    }
}

@Composable
internal fun ScanModeToggle(
    currentMode: ScanMode,
    onModeSelected: (ScanMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(MODE_BUTTON_CORNER_RADIUS))
            .background(Color.Black.copy(alpha = MODE_TOGGLE_BACKGROUND_ALPHA)),
    ) {
        ModeButton(
            label = "Barcode",
            icon = Icons.Default.QrCodeScanner,
            selected = currentMode == ScanMode.BARCODE,
            onClick = { onModeSelected(ScanMode.BARCODE) },
        )
        ModeButton(
            label = "Cover",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            selected = currentMode == ScanMode.COVER,
            onClick = { onModeSelected(ScanMode.COVER) },
        )
    }
}

@Composable
private fun ModeButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (selected) PrimaryTeal else Color.Transparent
    val tintColor = if (selected) {
        CardWhite
    } else {
        Color.White.copy(alpha = MODE_BUTTON_UNSELECTED_ALPHA)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(MODE_BUTTON_CORNER_RADIUS))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = MODE_BUTTON_HORIZONTAL_PADDING,
                vertical = MODE_BUTTON_VERTICAL_PADDING,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(MODE_BUTTON_ICON_SIZE),
        )
        Spacer(modifier = Modifier.width(MODE_BUTTON_TEXT_SPACING))
        Text(
            text = label,
            color = tintColor,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
internal fun DetectedTextOverlay(
    detectedText: String,
    isSearchingByText: Boolean,
    modifier: Modifier = Modifier,
) {
    val previewText = detectedText.take(DETECTED_TEXT_PREVIEW_LENGTH)
    val detectedLabel = "Detected: \"$previewText\""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DETECTED_TEXT_CARD_RADIUS))
            .background(Color.Black.copy(alpha = DETECTED_TEXT_BACKGROUND_ALPHA))
            .padding(DETECTED_TEXT_CONTENT_PADDING),
    ) {
        Column {
            Text(
                text = detectedLabel,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
            )
            if (isSearchingByText) {
                Spacer(modifier = Modifier.height(DETECTED_TEXT_SPACING))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryTeal,
                    trackColor = Color.White.copy(alpha = DETECTED_TEXT_TRACK_ALPHA),
                )
            }
        }
    }
}
