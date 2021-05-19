package com.johnturkson.sync.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.johnturkson.sync.data.Code

@Composable
fun Code(value: Pair<String, String>) {
    val (email, number) = value
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
        Text(
            text = email,
            style = TextStyle(fontSize = 20.sp)
        )
        Text(
            text = number,
            style = TextStyle(fontSize = 32.sp),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun Codes(codes: List<Pair<String, String>>) {
    LazyColumn {
        itemsIndexed(codes) { index, item ->
            Code(item)
            CodeDivider()
            if (index == codes.lastIndex) {
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
fun CodeListItem(code: Pair<String, String>) {
    Column(modifier = Modifier.clickable { /*TODO*/ }) {
        CodeSpacer()
        Code(value = code)
        CodeSpacer()
        CodeDivider()
    }
}

@Composable
fun SearchBarSpacer() {
    Spacer(Modifier.height(64.dp))
}

@Composable
fun GroupedCodes(codes: List<Code>) {
    // TODO move to function
    val grouped =
        codes.groupBy { code -> code.issuer }.toSortedMap().mapValues { (_, v) -> v.sortedBy { code -> code.account } }
    val keys = grouped.keys.toList().sorted()
    
    LazyColumn {
        item {
            SearchBarSpacer()
        }
        
        keys.forEachIndexed { index, key ->
            item {
                CodeHeader(key)
            }
            
            items(grouped[key] ?: emptyList()) { item ->
                CodeListItem(code = item.account to "123 456")
            }
            
            if (index == keys.lastIndex) {
                item {
                    Spacer(modifier = Modifier.height(64.dp).fillMaxWidth())
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewCode() {
    val code = "test@example.com" to "123456"
    Code(code)
}

@Composable
@Preview
fun PreviewCodes() {
    val codes = (1..20).map { index -> "test$index@example.com" to "123456" }.toList()
    Codes(codes)
}

@Composable
@Preview
fun PreviewGroupedCodes() {
    val codes = (1..50).map { index -> "test$index@example.com" to "123456" }.toList()
        .mapIndexed { index, pair -> Code("Issuer ${(index % 5)}", pair.first, "") }
    GroupedCodes(codes = codes)
}


// sort by alphabetical with ability to pin
// group by category (website) and allow collapsible groups
// search
