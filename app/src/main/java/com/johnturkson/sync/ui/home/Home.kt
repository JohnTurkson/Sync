package com.johnturkson.sync.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.johnturkson.sync.ui.TopBarScrollConnection
import com.johnturkson.sync.ui.state.CodeState
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val progress by viewModel.progress.collectAsState()
    val search by viewModel.search.collectAsState()
    val codes by viewModel.codes.collectAsState()
    val displayed by viewModel.displayed.collectAsState()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            navController = navController,
            codes = displayed,
            progress = progress,
            search = search,
            onSelect = { value -> viewModel.toggleSelection(value) },
            onSearchChange = { value -> viewModel.search(value) }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun HomeContent(
    navController: NavController,
    codes: List<CodeState>,
    progress: Float,
    search: String,
    onSelect: (CodeState) -> Unit,
    onSearchChange: (String) -> Unit,
) {
    val toolbarHeight = 72.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection =
        remember { TopBarScrollConnection(toolbarHeightPx, toolbarOffsetHeightPx) }
    val listState = rememberLazyListState()
    
    val context = LocalContext.current
    val permissionStatus = remember { mutableStateOf(false) }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionStatus.value = granted
        }
    
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            if (permissionStatus.value) {
                navController.navigate("Scanner")
            } else {
                val cameraPermissionCheck =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus.value = true
                    navController.navigate("Scanner")
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }) {
            
        }
    }) {
        Box(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)) {
            Column {
                CodeRefreshIndicator(progress)
                Codes(codes = codes, listState = listState, onSelect = onSelect)
            }
            
            CodeSearchBar(
                search,
                { value -> onSearchChange(value) },
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt())
                }
            )
        }
    }
}

@Composable
private fun requestCameraPermission(
    onPermissionDenied: () -> Unit = {},
    onPermissionGranted: () -> Unit = {},
) {
    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA
    val permissionCheck = ContextCompat.checkSelfPermission(context, permission)
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onPermissionGranted() else onPermissionDenied()
        }
    
    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted()
    } else {
        permissionLauncher.launch(permission)
    }
}

@Composable
fun CodeSearchBar(search: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = search,
        onValueChange = { searchValue -> onValueChange(searchValue) },
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        placeholder = { Text("Search") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CodeRefreshIndicator(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun Code(state: CodeState) {
    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()) {
        Text(
            text = "${state.account.issuer} (${state.account.name})",
            style = TextStyle(fontSize = 20.sp)
        )
        Text(
            text = if (state.isVisible) state.code else "------",
            style = TextStyle(fontSize = 32.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun CodeDivider() {
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun CodeHeader(header: String) {
    Text(
        text = header,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun CodeSpacer() {
    Spacer(Modifier.height(8.dp))
}

@Composable
fun CodeListItem(code: CodeState, onSelect: (CodeState) -> Unit) {
    Column(modifier = Modifier.clickable { onSelect(code) }) {
        CodeSpacer()
        Code(code)
        CodeSpacer()
        CodeDivider()
    }
}

@Composable
fun CodeOverflowSpacer() {
    Spacer(Modifier.height(64.dp))
}

@Composable
fun Codes(codes: List<CodeState>, listState: LazyListState, onSelect: (CodeState) -> Unit) {
    val grouped = codes.groupBy { code -> code.account.issuer }
        .toSortedMap()
        .mapValues { (_, v) -> v.sortedBy { code -> code.account.name } }
    val keys = grouped.keys.toList().sorted()
    
    LazyColumn(state = listState) {
        item {
            CodeOverflowSpacer()
        }
        
        keys.forEachIndexed { index, key ->
            item {
                CodeHeader(key)
            }
            
            items(grouped[key] ?: emptyList()) { code ->
                CodeListItem(code = code, onSelect = onSelect)
            }
            
            if (index == keys.lastIndex) {
                item {
                    CodeOverflowSpacer()
                }
            }
        }
    }
}
