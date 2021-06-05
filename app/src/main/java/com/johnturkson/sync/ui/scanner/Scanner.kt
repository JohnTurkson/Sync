package com.johnturkson.sync.ui.scanner

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.johnturkson.sync.image.SetupCodeAnalyzer

@Composable
fun Scanner(
    navController: NavController,
    viewModel: ScannerViewModel,
) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider = ProcessCameraProvider.getInstance(localContext)
    val handled = remember { mutableStateOf(false) }
    
    AndroidView(
        factory = { context ->
            val executor = ContextCompat.getMainExecutor(context)
            
            val previewView = PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            
            cameraProvider.addListener(
                {
                    val preview = Preview.Builder().build().apply { setSurfaceProvider(previewView.surfaceProvider) }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val analyzer = SetupCodeAnalyzer { account ->
                        if (!handled.value) {
                            handled.value = true
                            viewModel.onAccountSetup(account)
                            navController.popBackStack()
                        }
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply { setAnalyzer(executor, analyzer) }
                    
                    cameraProvider.get().apply {
                        unbindAll()
                        bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
                    }
                },
                executor
            )
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
    
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp).offset(y = -64.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(shape = RoundedCornerShape(8.dp)) {
            Text(text = "Point at Setup Code", modifier = Modifier.padding(8.dp))
        }
    }
}
