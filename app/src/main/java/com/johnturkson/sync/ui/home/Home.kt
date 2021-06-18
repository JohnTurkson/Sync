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
import androidx.compose.material.*
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.johnturkson.sync.data.Account
import com.johnturkson.sync.ui.TopBarScrollConnection
import com.johnturkson.sync.ui.state.CodeState
import kotlin.math.roundToInt

@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val progress by viewModel.progress.collectAsState()
    val search by viewModel.search.collectAsState()
    val codes by viewModel.codes.collectAsState()
    val pinned by viewModel.pinned.collectAsState()
    val displayed by viewModel.displayed.collectAsState()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            navController = navController,
            codes = displayed,
            pinned = pinned,
            progress = progress,
            search = search,
            onSelect = { value -> viewModel.toggleSelection(value) },
            onPin = { value -> viewModel.togglePin(value) },
            onSearchChange = { value -> viewModel.search(value) }
        )
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    codes: List<CodeState>,
    pinned: List<Account>,
    progress: Float,
    search: String,
    onSelect: (CodeState) -> Unit,
    onPin: (CodeState) -> Unit,
    onSearchChange: (String) -> Unit,
) {
    val toolbarHeight = 72.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember { TopBarScrollConnection(toolbarHeightPx, toolbarOffsetHeightPx) }
    val listState = rememberLazyListState()
    
    val context = LocalContext.current
    val permissionStatus = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionStatus.value = granted
    }
    
    
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            if (permissionStatus.value) {
                navController.navigate("Setup")
            } else {
                val cameraPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                    permissionStatus.value = true
                    navController.navigate("Setup")
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }) {
            
        }
    }) {
        Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            Column {
                CodeRefreshIndicator(progress)
                Codes(codes = codes, pinned = pinned, listState = listState, onSelect = onSelect, onPin = onPin)
            }
            
            // TODO remove transparency but preserve onSurface color
            CodeSearchBar(
                search,
                { value -> onSearchChange(value) },
                modifier = Modifier.offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
            )
        }
    }
}

@Composable
private fun requestCameraPermission(onPermissionDenied: () -> Unit = {}, onPermissionGranted: () -> Unit = {}) {
    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA
    val permissionCheck = ContextCompat.checkSelfPermission(context, permission)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
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
        modifier = modifier.padding(16.dp).fillMaxWidth(),
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
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp).fillMaxWidth()
    )
}

@Composable
fun Code(state: CodeState, onSelect: (CodeState) -> Unit, onPin: (CodeState) -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (issuer, code, pin) = createRefs()
        
        Text(
            modifier = Modifier.constrainAs(issuer) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(pin.start, 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            text = "${state.account.issuer} (${state.account.name})",
            style = TextStyle(fontSize = 20.sp)
        )
        
        Text(
            modifier = Modifier.constrainAs(code) {
                top.linkTo(issuer.bottom)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(pin.start, 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            text = if (state.isVisible) state.code else "------",
            style = TextStyle(fontSize = 32.sp),
            color = MaterialTheme.colors.primary
        )
        
        Button(onClick = { onPin(state) }, modifier = Modifier.constrainAs(pin) {
            top.linkTo(issuer.top)
            bottom.linkTo(code.bottom)
            start.linkTo(issuer.end, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }) {
            Text(text = if (state.isPinned) "pinned" else "pin")
        }
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
        style = MaterialTheme.typography.h6,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun CodeSpacer() {
    Spacer(Modifier.height(8.dp))
}

@Composable
fun CodeListItem(code: CodeState, onSelect: (CodeState) -> Unit, onPin: (CodeState) -> Unit) {
    Column(modifier = Modifier.clickable { onSelect(code) }) {
        CodeSpacer()
        Code(code, onSelect, onPin)
        CodeSpacer()
        CodeDivider()
    }
}

@Composable
fun CodeOverflowSpacer() {
    Spacer(Modifier.height(64.dp))
}

@Composable
fun Codes(
    codes: List<CodeState>,
    pinned: List<Account>,
    listState: LazyListState,
    onSelect: (CodeState) -> Unit,
    onPin: (CodeState) -> Unit,
) {
    val grouped = codes
        .filter { code -> code.account !in pinned }
        .groupBy { code -> code.account.issuer }
        .toSortedMap()
        .mapValues { (_, v) -> v.sortedBy { code -> code.account.name } }
    val keys = grouped.keys.toList().sorted()
    
    val groupedPins = codes.filter { code -> code.account in pinned }
        .groupBy { code -> code.account.issuer }
        .toSortedMap()
        .mapValues { (_, v) -> v.sortedBy { code -> code.account.name } }
    val pinnedKeys = groupedPins.keys.toList().sorted()
    
    LazyColumn(state = listState) {
        item {
            CodeOverflowSpacer()
        }
        
        if (pinnedKeys.isNotEmpty()) {
            item {
                Text("Pins", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        
        pinnedKeys.forEachIndexed { index, key ->
            item {
                CodeHeader(key)
            }
            
            items(groupedPins[key] ?: emptyList()) { code ->
                CodeListItem(code = code, onSelect = onSelect, onPin = onPin)
            }
        }
        
        keys.forEachIndexed { index, key ->
            item {
                CodeHeader(key)
            }
            
            items(grouped[key] ?: emptyList()) { code ->
                CodeListItem(code = code, onSelect = onSelect, onPin = onPin)
            }
        }
        
        
        item {
            CodeOverflowSpacer()
        }
    }
}
