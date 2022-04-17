package com.johnturkson.sync.android.ui.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.johnturkson.sync.android.data.Account
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun Home(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val progress by viewModel.progressState.progress.collectAsState()
    val search by viewModel.searchState.collectAsState()
    val codes by viewModel.codes.collectAsState()
    val displayed by viewModel.displayed.collectAsState()
    
    var searchHasFocus by remember { mutableStateOf(false) }
    var accountsOverflow by remember { mutableStateOf(false) }
    val searchScrollable by remember(search, searchHasFocus, accountsOverflow) {
        derivedStateOf { search == "" && !searchHasFocus && accountsOverflow }
    }
    val searchScrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior { searchScrollable } }
    
    Column {
        RefreshIndicator(progress = progress)
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
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("Setup") }) {
                    Icon(Icons.Default.Add, "Add an Account")
                }
            },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Accounts(
                        accounts = displayed,
                        codes = codes,
                        onAccountsOverflow = { overflow -> accountsOverflow = overflow },
                    )
                }
            }
        )
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
    
    CenterAlignedTopAppBar(
        title = {
            OutlinedTextField(
                searchQuery,
                onValueChange = { change -> onSearchQueryChange(change) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> onSearchFocusChange(focusState.isFocused) }
                    .fillMaxWidth(),
                placeholder = { Text("Search", fontSize = MaterialTheme.typography.bodyLarge.fontSize) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    keyboard?.hide()
                    focusManager.clearFocus()
                },
                trailingIcon = {
                    if (searchQuery != "") {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, "Clear Search")
                        }
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

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun Accounts(
    accounts: List<Account>,
    codes: Map<Account, String>,
    onAccountsOverflow: (Boolean) -> Unit,
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        val scrollSafetyMarginItems = 5
        snapshotFlow { accounts.size > listState.layoutInfo.visibleItemsInfo.size + scrollSafetyMarginItems }
            .distinctUntilChanged()
            .collect { overflow -> onAccountsOverflow(overflow) }
    }
    
    LazyColumn(state = listState) {
        items(accounts) { account ->
            Account(account = account, code = codes[account] ?: "")
            Divider(modifier = Modifier.padding(8.dp))
        }
        
        item {
            Box(modifier = Modifier.fillMaxWidth().height(64.dp))
        }
    }
}

@Composable
fun Account(account: Account, code: String) {
    Column(modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()) {
        Text("${account.issuer} (${account.name})")
        Text(
            code,
            color = MaterialTheme.colorScheme.primary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
        )
    }
}
