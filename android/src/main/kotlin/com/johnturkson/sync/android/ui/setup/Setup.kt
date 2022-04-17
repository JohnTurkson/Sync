package com.johnturkson.sync.android.ui.setup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.johnturkson.sync.android.data.Account
import com.johnturkson.sync.android.image.SetupCodeAnalyzer

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@Composable
fun Setup(navController: NavController, viewModel: SetupViewModel) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var cameraPermissionRequested by rememberSaveable(cameraPermissionState) { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted && !cameraPermissionRequested) {
            cameraPermissionState.launchPermissionRequest()
            cameraPermissionRequested = true
        }
    }
    
    Scaffold(
        bottomBar = {
            CameraGuidance(
                cameraPermissionGranted = cameraPermissionState.status.isGranted,
                cameraPermissionRequested = cameraPermissionRequested,
                onRequestCameraPermission = {
                    cameraPermissionState.launchPermissionRequest()
                    cameraPermissionRequested = true
                },
            )
        },
        content = {
            CameraPreview(
                onCameraResult = { account ->
                    viewModel.onAccountSetup(account)
                    navController.popBackStack()
                },
                onCameraClosed = {
                    navController.popBackStack()
                },
            )
        },
    )
}

@Composable
fun CameraPreview(onCameraResult: (Account) -> Unit, onCameraClosed: () -> Unit) {
    val localContext = LocalContext.current
    val localLifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider = ProcessCameraProvider.getInstance(localContext)
    var cameraHandled by rememberSaveable { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                val executor = ContextCompat.getMainExecutor(context)
                
                val previewView = PreviewView(context).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                
                cameraProvider.addListener(
                    {
                        val preview =
                            Preview.Builder().build().apply { setSurfaceProvider(previewView.surfaceProvider) }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        val analyzer = SetupCodeAnalyzer { account ->
                            if (!cameraHandled) {
                                onCameraResult(account)
                                cameraHandled = true
                            }
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply { setAnalyzer(executor, analyzer) }
                        
                        cameraProvider.get().apply {
                            unbindAll()
                            bindToLifecycle(localLifecycleOwner, cameraSelector, imageAnalysis, preview)
                        }
                    },
                    executor,
                )
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        IconButton(onClick = { onCameraClosed() }) {
            Icon(Icons.Default.Close, contentDescription = "Exit Setup")
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun CameraGuidance(
    cameraPermissionGranted: Boolean,
    cameraPermissionRequested: Boolean,
    onRequestCameraPermission: () -> Unit,
) {
    fun navigateToAppSettings(context: Context) {
        startActivity(
            context,
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ),
            null,
        )
    }
    
    val context = LocalContext.current
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(shape = RoundedCornerShape(8.dp)) {
            Text(
                if (cameraPermissionGranted) "Point at Setup Code" else "Camera Permission Denied",
                modifier = Modifier.padding(8.dp)
            )
        }
        
        Box(
            modifier = Modifier.height(128.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!cameraPermissionGranted) {
                Button(
                    onClick = {
                        if (cameraPermissionRequested) {
                            navigateToAppSettings(context)
                        } else {
                            onRequestCameraPermission()
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Enable Camera")
                }
            }
        }
    }
}
