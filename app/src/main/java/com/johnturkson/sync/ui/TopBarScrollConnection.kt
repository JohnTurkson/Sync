package com.johnturkson.sync.ui

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

class TopBarScrollConnection(
    private val searchBarHeightPx: Float,
    private val searchBarOffsetHeightPx: MutableState<Float>,
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        val newOffset = searchBarOffsetHeightPx.value + delta
        searchBarOffsetHeightPx.value = newOffset.coerceIn(-searchBarHeightPx, 0f)
        return Offset.Zero
    }
}
