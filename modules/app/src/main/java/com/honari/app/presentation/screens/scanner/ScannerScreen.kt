package com.honari.app.presentation.screens.scanner

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.CardWhite
import com.honari.app.presentation.theme.ErrorRed
import com.honari.app.presentation.theme.PrimaryTeal
import com.honari.app.presentation.theme.TextSecondary
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val YEAR_CHARACTERS = 4
private const val ANALYSIS_WIDTH = 1_280
private const val ANALYSIS_HEIGHT = 720
private const val BACK_BUTTON_ALPHA = 0.4f
private val BACK_BUTTON_PADDING = 8.dp
private val CAMERA_PERMISSION_PADDING = 32.dp
private val OVERLAY_HORIZONTAL_PADDING = 24.dp
private val MODE_TOGGLE_TOP_PADDING = 56.dp
private val DETECTED_TEXT_BOTTOM_PADDING = 120.dp
private val SNACKBAR_PADDING = 16.dp

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
    val instructionText = if (uiState.scanMode == ScanMode.BARCODE) {
        "Point camera at book barcode"
    } else {
        "Point camera at book title or cover"
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermission.status.isGranted) {
            CameraPreview(
                scanMode = uiState.scanMode,
                onBarcodeDetected = viewModel::onBarcodeScanned,
                onTextDetected = viewModel::onTextDetected,
            )
            ScannerOverlay(instruction = instructionText)
            ScanModeToggle(
                currentMode = uiState.scanMode,
                onModeSelected = viewModel::switchMode,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = MODE_TOGGLE_TOP_PADDING),
            )
            if (uiState.scanMode == ScanMode.COVER && uiState.detectedText.isNotEmpty()) {
                DetectedTextOverlay(
                    detectedText = uiState.detectedText,
                    isSearchingByText = uiState.isSearchingByText,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = DETECTED_TEXT_BOTTOM_PADDING)
                        .padding(horizontal = OVERLAY_HORIZONTAL_PADDING),
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Camera permission required to scan books.",
                    color = Color.White,
                    modifier = Modifier.padding(CAMERA_PERMISSION_PADDING),
                )
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(BACK_BUTTON_PADDING)
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
                .padding(SNACKBAR_PADDING),
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
private fun CameraPreview(
    scanMode: ScanMode,
    onBarcodeDetected: (String) -> Unit,
    onTextDetected: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }

    DisposableEffect(previewView, lifecycleOwner, scanMode) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val barcodeScanner = BarcodeScanning.getClient()
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val barcodeExecutor = Executors.newSingleThreadExecutor()
        val textExecutor = Executors.newSingleThreadExecutor()

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { previewUseCase ->
                previewUseCase.surfaceProvider = previewView.surfaceProvider
            }
            val imageAnalyzer = when (scanMode) {
                ScanMode.BARCODE -> {
                    buildBarcodeAnalyzer(
                        executor = barcodeExecutor,
                        barcodeScanner = barcodeScanner,
                        onBarcodeDetected = onBarcodeDetected,
                    )
                }

                ScanMode.COVER -> {
                    buildTextAnalyzer(
                        executor = textExecutor,
                        recognizer = textRecognizer,
                        onTextDetected = onTextDetected,
                    )
                }
            }

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
            textRecognizer.close()
            barcodeExecutor.shutdown()
            textExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
    )
}

private fun buildBarcodeAnalyzer(
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

private fun buildTextAnalyzer(
    executor: ExecutorService,
    recognizer: TextRecognizer,
    onTextDetected: (String) -> Unit,
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
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text.trim()
                        if (text.isNotBlank()) {
                            onTextDetected(text)
                        }
                    }
                    .addOnCompleteListener { imageProxy.close() }
            } else {
                imageProxy.close()
            }
        }
    }
