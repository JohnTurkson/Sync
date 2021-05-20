package com.johnturkson.sync.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johnturkson.sync.data.Account
import kotlin.math.roundToInt

@Composable
fun Home() {
    val viewModel = viewModel(HomeViewModel::class.java)
    val progress by viewModel.progress.collectAsState()
    val search by viewModel.search.collectAsState()
    val codes by viewModel.codes.collectAsState()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            codes = codes,
            progress = progress,
            search = search,
            onSelect = { value -> viewModel.toggleSelection(value) },
            onSearchChange = { value -> viewModel.search(value) }
        )
    }
}

@Composable
fun HomeContent(
    codes: List<CodeState>,
    progress: Float,
    search: String,
    onSelect: (CodeState) -> Unit,
    onSearchChange: (String) -> Unit,
) {
    val toolbarHeight = 72.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember { TopBarScrollConnection(toolbarHeightPx, toolbarOffsetHeightPx) }
    
    Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
        Column {
            CodeRefreshIndicator(progress)
            GroupedCodes(codes = codes, onSelect = onSelect)
        }
        CodeSearchBar(
            search,
            { value -> onSearchChange(value) },
            modifier = Modifier.offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
        )
    }
}

@Composable
fun CodeSearchBar(search: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = search,
        onValueChange = { searchValue -> onValueChange(searchValue) },
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp).fillMaxWidth(),
        placeholder = { Text("Search") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
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
fun Code(state: CodeState) {
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        Text(
            text = "${state.account.issuer} (${state.account.name})",
            style = TextStyle(fontSize = 20.sp)
        )
        Text(
            text = if (state.isVisible) state.code else "------",
            style = TextStyle(fontSize = 32.sp),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun Codes(value: List<Account>) {
    LazyColumn {
        itemsIndexed(value) { index, item ->
            // Code(item)
            CodeDivider()
            if (index == value.lastIndex) {
                Spacer(modifier = Modifier.height(64.dp).fillMaxWidth())
            }
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
fun GroupedCodes(codes: List<CodeState>, onSelect: (CodeState) -> Unit) {
    val grouped = codes.groupBy { code -> code.account.issuer }
        .toSortedMap()
        .mapValues { (_, v) -> v.sortedBy { code -> code.account.name } }
    val keys = grouped.keys.toList().sorted()
    
    LazyColumn {
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
