package com.johnturkson.sync.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.johnturkson.sync.data.Account
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val progress by viewModel.refreshState.progress.collectAsState()
    val search by viewModel.searchState.collectAsState()
    val codes by viewModel.codes.collectAsState()
    val displayed by viewModel.displayed.collectAsState()
    
    var searchHasFocus by remember { mutableStateOf(false) }
    var accountsOverflow by remember { mutableStateOf(false) }
    val searchScrollable by remember(search, searchHasFocus, accountsOverflow) {
        derivedStateOf { search == "" && !searchHasFocus && accountsOverflow }
    }
    val searchScrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior { searchScrollable } }
    
    Scaffold(
        modifier = Modifier.nestedScroll(searchScrollBehavior.nestedScrollConnection),
        topBar = {
            SearchBar(
                searchQuery = search,
                onSearchQueryChange = viewModel::setSearchState,
                searchHasFocus = searchHasFocus,
                onSearchFocusChange = { focused -> searchHasFocus = focused },
                scrollable = searchScrollable,
                scrollBehavior = searchScrollBehavior,
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                RefreshIndicator(progress = progress)
                Accounts(
                    accounts = displayed,
                    codes = codes,
                    onAccountsOverflow = { overflow -> accountsOverflow = overflow },
                )
            }
        }
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchHasFocus: Boolean,
    onSearchFocusChange: (Boolean) -> Unit,
    scrollable: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(scrollable) {
        if (!scrollable) scrollBehavior.offset = 0f
    }
    
    SmallTopAppBar(
        title = {
            OutlinedTextField(
                searchQuery,
                onValueChange = { change -> onSearchQueryChange(change) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { change -> onSearchFocusChange(change.isFocused) }
                    .fillMaxWidth(),
                placeholder = { Text("Search") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    keyboard?.hide()
                    focusManager.clearFocus()
                },
                trailingIcon = {
                    if (searchQuery != "") {
                        Icon(
                            Icons.Default.Close,
                            "Clear Search",
                            modifier = Modifier.clickable { onSearchQueryChange("") },
                        )
                    }
                },
            )
        },
        scrollBehavior = scrollBehavior,
    )
    
    BackHandler(searchHasFocus) {
        keyboard?.hide()
        onSearchQueryChange("")
        focusManager.clearFocus()
    }
}

@Composable
fun RefreshIndicator(progress: Float) {
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier.fillMaxWidth()
    )
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun Accounts(
    accounts: List<Account>,
    codes: Map<Account, String>,
    onAccountsOverflow: (Boolean) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(accounts) { account ->
            Account(account = account, code = codes[account] ?: "")
        }
        
        item {
            Box(modifier = Modifier.fillMaxWidth().height(64.dp))
        }
    }
    
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        val scrollSafetyMarginItems = 5
        snapshotFlow { accounts.size > listState.layoutInfo.visibleItemsInfo.size + scrollSafetyMarginItems }
            .distinctUntilChanged()
            .collect { overflow -> onAccountsOverflow(overflow) }
    }
}

@Composable
fun Account(account: Account, code: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
    ) {
        Text("${account.issuer} (${account.name})")
        Text(code)
    }
}

// @ExperimentalMaterial3Api
// @Composable
// fun HomeContent(
//     navController: NavController,
//     codes: List<CodeState>,
//     progress: Float,
//     search: String,
//     onSelect: (CodeState) -> Unit,
//     onSearchChange: (String) -> Unit,
// ) {
//     val toolbarHeight = 72.dp
//     val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
//     val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
//     val nestedScrollConnection =
//         remember { TopBarScrollConnection(toolbarHeightPx, toolbarOffsetHeightPx) }
//     val listState = rememberLazyListState()
//
//     val context = LocalContext.current
//     val permissionStatus = remember { mutableStateOf(false) }
//     val permissionLauncher =
//         rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//             permissionStatus.value = granted
//         }
//
//     Scaffold(floatingActionButton = {
//         FloatingActionButton(onClick = {
//             if (permissionStatus.value) {
//                 navController.navigate("Scanner")
//             } else {
//                 val cameraPermissionCheck =
//                     ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//                 if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED) {
//                     permissionStatus.value = true
//                     navController.navigate("Scanner")
//                 } else {
//                     permissionLauncher.launch(Manifest.permission.CAMERA)
//                 }
//             }
//         }) {
//
//         }
//     }) {
//         Box(modifier = Modifier
//             .fillMaxSize()
//             .nestedScroll(nestedScrollConnection)) {
//             Column {
//                 CodeRefreshIndicator(progress)
//                 Codes(codes = codes, listState = listState, onSelect = onSelect)
//             }
//
//             CodeSearchBar(
//                 search,
//                 { value -> onSearchChange(value) },
//                 modifier = Modifier.offset {
//                     IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt())
//                 }
//             )
//         }
//     }
// }
//
// @Composable
// private fun requestCameraPermission(
//     onPermissionDenied: () -> Unit = {},
//     onPermissionGranted: () -> Unit = {},
// ) {
//     val context = LocalContext.current
//     val permission = Manifest.permission.CAMERA
//     val permissionCheck = ContextCompat.checkSelfPermission(context, permission)
//     val permissionLauncher =
//         rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//             if (granted) onPermissionGranted() else onPermissionDenied()
//         }
//
//     if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//         onPermissionGranted()
//     } else {
//         permissionLauncher.launch(permission)
//     }
// }
//
// @Composable
// fun CodeSearchBar(search: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
//     TextField(
//         value = search,
//         onValueChange = { searchValue -> onValueChange(searchValue) },
//         modifier = modifier
//             .padding(16.dp)
//             .fillMaxWidth(),
//         placeholder = { Text("Search") },
//         singleLine = true,
//         shape = RoundedCornerShape(8.dp),
//         colors = TextFieldDefaults.textFieldColors(
//             focusedIndicatorColor = Color.Transparent,
//             unfocusedIndicatorColor = Color.Transparent
//         )
//     )
// }
//
// @Composable
// fun CodeRefreshIndicator(progress: Float, modifier: Modifier = Modifier) {
//     LinearProgressIndicator(
//         progress = progress,
//         modifier = modifier
//             .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
//             .fillMaxWidth()
//     )
// }
//
// @Composable
// fun Code(state: CodeState) {
//     Column(modifier = Modifier
//         .padding(horizontal = 16.dp)
//         .fillMaxWidth()) {
//         Text(
//             text = "${state.account.issuer} (${state.account.name})",
//             style = TextStyle(fontSize = 20.sp)
//         )
//         Text(
//             text = if (state.isVisible) state.code else "------",
//             style = TextStyle(fontSize = 32.sp),
//             color = MaterialTheme.colorScheme.primary
//         )
//     }
// }
//
// @Composable
// fun CodeDivider() {
//     Divider(modifier = Modifier.padding(horizontal = 16.dp))
// }
//
// @Composable
// fun CodeHeader(header: String) {
//     Text(
//         text = header,
//         style = MaterialTheme.typography.headlineSmall,
//         modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
//     )
// }
//
// @Composable
// fun CodeSpacer() {
//     Spacer(Modifier.height(8.dp))
// }
//
// @Composable
// fun CodeListItem(code: CodeState, onSelect: (CodeState) -> Unit) {
//     Column(modifier = Modifier.clickable { onSelect(code) }) {
//         CodeSpacer()
//         Code(code)
//         CodeSpacer()
//         CodeDivider()
//     }
// }
//
// @Composable
// fun CodeOverflowSpacer() {
//     Spacer(Modifier.height(64.dp))
// }
//
// @Composable
// fun Codes(codes: List<CodeState>, listState: LazyListState, onSelect: (CodeState) -> Unit) {
//     val grouped = codes.groupBy { code -> code.account.issuer }
//         .toSortedMap()
//         .mapValues { (_, v) -> v.sortedBy { code -> code.account.name } }
//     val keys = grouped.keys.toList().sorted()
//
//     LazyColumn(state = listState) {
//         item {
//             CodeOverflowSpacer()
//         }
//
//         keys.forEachIndexed { index, key ->
//             item {
//                 CodeHeader(key)
//             }
//
//             items(grouped[key] ?: emptyList()) { code ->
//                 CodeListItem(code = code, onSelect = onSelect)
//             }
//
//             if (index == keys.lastIndex) {
//                 item {
//                     CodeOverflowSpacer()
//                 }
//             }
//         }
//     }
// }
