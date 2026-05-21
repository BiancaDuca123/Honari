package com.honari.app.presentation.screens.scanner

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextSecondary
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.ui.geometry.Size as CanvasSize

private const val YEAR_CHARACTERS = 4
private const val ANALYSIS_WIDTH = 1_280
private const val ANALYSIS_HEIGHT = 720
private const val OVERLAY_WIDTH_FRACTION = 0.78f
private const val OVERLAY_HEIGHT_FRACTION = 0.18f
private const val OVERLAY_CORNER_RADIUS = 36f
private const val OVERLAY_ALPHA = 0.55f
private const val BACK_BUTTON_ALPHA = 0.4f

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onBack: () -> Unit = {},
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermission.status.isGranted) {
            CameraPreview(onBarcodeDetected = viewModel::onBarcodeScanned)
            ScannerOverlay()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Camera permission required to scan books.",
                    color = Color.White,
                    modifier = Modifier.padding(32.dp),
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
                .background(
                    color = Color.Black.copy(alpha = BACK_BUTTON_ALPHA),
                    shape = CircleShape,
                ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(containerColor = ErrorRed, contentColor = CardWhite) {
                Text(text = data.visuals.message)
            }
        }
    }

    if (uiState.isLoading || uiState.scannedBook != null) {
        ModalBottomSheet(
            onDismissRequest = viewModel::dismissSheet,
            sheetState = sheetState,
            containerColor = CardWhite,
        ) {
            BookInfoSheet(
                uiState = uiState,
                onAddToLibrary = viewModel::addToLibrary,
            )
        }
    }
}

private val SHEET_HORIZONTAL_PADDING = 24.dp
private val SHEET_BOTTOM_PADDING = 32.dp
private val BOOK_COVER_WIDTH = 80.dp
private val BOOK_COVER_HEIGHT = 120.dp
private val BOOK_COVER_RADIUS = 8.dp
private val BOOK_DETAILS_SPACING = 16.dp
private val BOOK_METADATA_SPACING = 4.dp
private val DESCRIPTION_TOP_SPACING = 16.dp
private val STATUS_TOP_SPACING = 24.dp
private val STATUS_MESSAGE_TOP_SPACING = 8.dp
private val ACTIONS_SPACING = 12.dp
private val STATUS_CHIP_HORIZONTAL_PADDING = 12.dp
private val STATUS_CHIP_VERTICAL_PADDING = 8.dp
private val STATUS_ICON_TEXT_SPACING = 8.dp
private val STATUS_ICON_SIZE = 18.dp
private val ACTION_BORDER_WIDTH = 1.dp
private val LOADING_CONTENT_PADDING = 48.dp
private const val TITLE_MAX_LINES = 2
private const val DESCRIPTION_MAX_LINES = 5
private const val STATUS_CHIP_BACKGROUND_ALPHA = 0.12f

@Composable
private fun BookInfoSheet(
    uiState: ScannerUiState,
    onAddToLibrary: (ReadingStatus) -> Unit,
) {
    if (uiState.isLoading) {
        LoadingBookInfoSheet()
        return
    }

    val book = uiState.scannedBook ?: return

    Column(
        modifier = Modifier.padding(
            start = SHEET_HORIZONTAL_PADDING,
            end = SHEET_HORIZONTAL_PADDING,
            bottom = SHEET_BOTTOM_PADDING,
        ),
    ) {
        BookHeader(book = book)
        BookDescription(description = book.description)
        LibraryStatusSection(uiState = uiState, onAddToLibrary = onAddToLibrary)
    }
}

@Composable
private fun LoadingBookInfoSheet() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = SHEET_HORIZONTAL_PADDING,
                end = SHEET_HORIZONTAL_PADDING,
                bottom = LOADING_CONTENT_PADDING,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PrimaryTeal)
    }
}

@Composable
private fun BookHeader(book: Book) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier
                .size(width = BOOK_COVER_WIDTH, height = BOOK_COVER_HEIGHT)
                .clip(RoundedCornerShape(BOOK_COVER_RADIUS)),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(BOOK_DETAILS_SPACING))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrownHeadline,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            if (book.authors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(BOOK_METADATA_SPACING))
                Text(
                    text = book.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic,
                )
            }
            buildMetadata(book).takeIf { it.isNotBlank() }?.let { metadata ->
                Spacer(modifier = Modifier.height(BOOK_METADATA_SPACING))
                Text(
                    text = metadata,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun BookDescription(description: String) {
    if (description.isEmpty()) {
        return
    }

    Spacer(modifier = Modifier.height(DESCRIPTION_TOP_SPACING))
    Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary,
        maxLines = DESCRIPTION_MAX_LINES,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun LibraryStatusSection(
    uiState: ScannerUiState,
    onAddToLibrary: (ReadingStatus) -> Unit,
) {
    Spacer(modifier = Modifier.height(STATUS_TOP_SPACING))
    if (uiState.isInLibrary) {
        LibraryStatusChip()
        uiState.addedMessage?.let { addedMessage ->
            Spacer(modifier = Modifier.height(STATUS_MESSAGE_TOP_SPACING))
            Text(
                text = addedMessage,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ACTIONS_SPACING),
    ) {
        Button(
            onClick = { onAddToLibrary(ReadingStatus.READ) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryTeal,
                contentColor = CardWhite,
            ),
        ) {
            Text("Mark as Read")
        }
        OutlinedButton(
            onClick = { onAddToLibrary(ReadingStatus.WANT_TO_READ) },
            modifier = Modifier.weight(1f),
            border = BorderStroke(ACTION_BORDER_WIDTH, PrimaryTeal),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryTeal,
            ),
        ) {
            Text("Add to Wishlist")
        }
    }
}

@Composable
private fun LibraryStatusChip() {
    Surface(
        color = PrimaryTeal.copy(alpha = STATUS_CHIP_BACKGROUND_ALPHA),
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = STATUS_CHIP_HORIZONTAL_PADDING,
                vertical = STATUS_CHIP_VERTICAL_PADDING,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier.size(STATUS_ICON_SIZE),
            )
            Spacer(modifier = Modifier.width(STATUS_ICON_TEXT_SPACING))
            Text(
                text = "Already in your library",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryTeal,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun buildMetadata(book: Book): String {
    val parts = mutableListOf<String>()
    val year = book.publishedDate.take(YEAR_CHARACTERS)
    if (year.isNotBlank()) {
        parts.add(year)
    }
    if (book.pageCount > 0) {
        parts.add("${book.pageCount} pages")
    }
    if (book.language.isNotBlank()) {
        parts.add(book.language.uppercase())
    }
    return parts.joinToString(" · ")
}

@Composable
private fun CameraPreview(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(previewView, lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val barcodeScanner = BarcodeScanning.getClient()

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val imageAnalyzer = buildImageAnalyzer(executor, barcodeScanner, onBarcodeDetected)

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer,
            )
        }

        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
            barcodeScanner.close()
            executor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
    )
}

private fun buildImageAnalyzer(
    executor: ExecutorService,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit,
): ImageAnalysis = ImageAnalysis.Builder()
    .setTargetResolution(Size(ANALYSIS_WIDTH, ANALYSIS_HEIGHT))
    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
    .build()
    .also { analysis ->
        analysis.setAnalyzer(executor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees,
                )
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val code = barcodes.firstNotNullOfOrNull { barcode ->
                            barcode.rawValue?.takeIf {
                                barcode.format == Barcode.FORMAT_EAN_13 ||
                                    barcode.format == Barcode.FORMAT_EAN_8 ||
                                    barcode.format == Barcode.FORMAT_UPC_A ||
                                    barcode.format == Barcode.FORMAT_UPC_E ||
                                    barcode.valueType == Barcode.TYPE_ISBN
                            }
                        }
                        code?.let(onBarcodeDetected)
                    }
                    .addOnCompleteListener { imageProxy.close() }
            } else {
                imageProxy.close()
            }
        }
    }

@Composable
private fun ScannerOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
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
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(rect.left, rect.top),
                size = CanvasSize(rect.width, rect.height),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 5f),
                blendMode = BlendMode.SrcOver,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Point camera at book barcode",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 160.dp),
            )
        }
    }
}
