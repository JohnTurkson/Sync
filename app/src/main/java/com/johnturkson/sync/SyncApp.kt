package com.johnturkson.sync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.johnturkson.sync.theme.AppTheme
import com.johnturkson.sync.ui.CodeViewModel
import com.johnturkson.sync.ui.PreviewGroupedCodes
import com.johnturkson.sync.ui.SearchBarScrollConnection
import kotlin.math.roundToInt

@Composable
fun SyncApp() {
    AppTheme {
        val viewModel = viewModel(CodeViewModel::class.java)
        val progress by viewModel.progress.collectAsState()
        val search by viewModel.search.collectAsState()
        val codes by viewModel.codes.collectAsState(initial = emptyList<com.johnturkson.sync.data.Code>())
        
        val toolbarHeight = 72.dp
        val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
        val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
        
        val nestedScrollConnection = remember { SearchBarScrollConnection(toolbarHeightPx, toolbarOffsetHeightPx) }
        
        Scaffold {
            Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
                Column {
                    RefreshBar(progress)
                    PreviewGroupedCodes()
                }
                SearchBar(
                    search,
                    { value -> viewModel.search(value) },
                    modifier = Modifier.offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
                )
            }
        }
    }
}

@Composable
fun SearchBar(search: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
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
fun RefreshBar(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp).fillMaxWidth()
    )
}
