package com.honari.app.presentation.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.honari.app.R
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.presentation.navigation.Screen
import com.honari.app.presentation.theme.HonariTheme
import com.honari.app.presentation.theme.PrimaryColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.camera.core.Preview as CameraPreview

@Composable
fun ScanScreen(navController: NavController, viewModel: ScanViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(uiState.bookAddedToLibrary) {
        if (uiState.bookAddedToLibrary) {
            scope.launch {
                snackbarHostState.showSnackbar("Book added to your library!")
                viewModel.acknowledgeLibraryAdd()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!hasCameraPermission) {
            PermissionRequestView(
                onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onBack = { navController.popBackStack() }
            )
        } else {
            ScanCameraContent(
                uiState = uiState,
                navController = navController,
                viewModel = viewModel
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = PrimaryColor,
                contentColor = Color.White
            )
        }
    }
}

@Composable
private fun ScanCameraContent(
    uiState: ScanUiState,
    navController: NavController,
    viewModel: ScanViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewView(
            isScanning = uiState.isScanning && !uiState.isLoading,
            onBarcodeDetected = viewModel::onBarcodeDetected
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    )
                )
        )
        ScanTopBar(
            onBack = { navController.popBackStack() },
            onToggleSearch = viewModel::toggleManualSearch,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
        if (uiState.isScanning && !uiState.isLoading) {
            ScanReticle(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
        if (uiState.isLoading) LoadingOverlay(modifier = Modifier.align(Alignment.Center))
        uiState.error?.let { err ->
            ErrorBanner(
                message = err,
                onDismiss = viewModel::clearError,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .padding(horizontal = 24.dp)
            )
        }
        AnimatedVisibility(
            visible = uiState.showManualSearch,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ManualSearchPanel(
                query = uiState.manualQuery,
                results = uiState.searchResults,
                isLoading = uiState.isLoading,
                onQueryChanged = viewModel::onQueryChanged,
                onSearch = viewModel::onSearch,
                onResultSelected = viewModel::onSearchResultSelected,
                onClose = viewModel::toggleManualSearch
            )
        }
        AnimatedVisibility(
            visible = uiState.scannedBook != null && !uiState.showManualSearch,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            uiState.scannedBook?.let { book ->
                BookResultSheet(
                    book = book,
                    onAddToLibrary = { viewModel.addToLibrary(book, ReadingStatus.WANT_TO_READ) },
                    onViewDetails = {
                        navController.navigate(Screen.BookDetail.createRoute(book.id))
                    },
                    onClose = viewModel::resetScan,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
private fun CameraPreviewView(isScanning: Boolean, onBarcodeDetected: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val analyzer = remember { BarcodeAnalyzer(onBarcodeDetected) }
    val previewView = remember { PreviewView(context) }
    LaunchedEffect(isScanning) { if (isScanning) analyzer.reset() }
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
    LaunchedEffect(lifecycleOwner) {
        val cameraProvider =
            withContext(Dispatchers.IO) { ProcessCameraProvider.getInstance(context).get() }
        val preview =
            CameraPreview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
        val imageAnalysis =
            ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer) }
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (_: Exception) {
            // no-op
        }
    }
}

@Composable
private fun ScanTopBar(
    onBack: () -> Unit,
    onToggleSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Scan a Book",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = onToggleSearch,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.manual_search),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ScanReticle(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line_y"
    )
    Box(modifier = modifier.size(260.dp, 160.dp), contentAlignment = Alignment.Center) {
        CornerBrackets()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = ((-70 + 140 * scanLineY)).dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            PrimaryColor.copy(alpha = 0.9f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun CornerBrackets() {
    val c = Color.White
    val s = 24.dp
    val w = 3.dp
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(s)
                .border(w, c, RoundedCornerShape(topStart = 8.dp))
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(s)
                .border(w, c, RoundedCornerShape(topEnd = 8.dp))
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .size(s)
                .border(w, c, RoundedCornerShape(bottomStart = 8.dp))
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(s)
                .border(w, c, RoundedCornerShape(bottomEnd = 8.dp))
        )
    }
}

@Composable
private fun LoadingOverlay(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
        Text(
            text = "Identifying book\u2026",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB00020).copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.dismiss),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestView(onRequest: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = stringResource(R.string.grant_camera_permission),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Honari needs camera access to scan book barcodes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.grant_permission_button),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Scan – Permission Request Light", showBackground = true, showSystemUi = true)
@Composable
private fun ScanPermissionLightPreview() {
    HonariTheme(darkTheme = false) {
        PermissionRequestView(onRequest = {}, onBack = {})
    }
}

@Preview(
    name = "Scan – Permission Request Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ScanPermissionDarkPreview() {
    HonariTheme(darkTheme = true) {
        PermissionRequestView(onRequest = {}, onBack = {})
    }
}
