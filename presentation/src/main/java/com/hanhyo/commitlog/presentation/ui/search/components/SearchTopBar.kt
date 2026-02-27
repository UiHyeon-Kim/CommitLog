package com.hanhyo.commitlog.presentation.ui.search.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hanhyo.commitlog.presentation.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    // 화면 진입 시 키보드 올리기
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        stringResource(R.string.search_placeholder),
                        style = CommitLogTheme.typography.bodyLarge,
                        color = CommitLogTheme.colors.textTertiary
                    )
                },
                singleLine = true,
                textStyle = CommitLogTheme.typography.bodyLarge.copy(
                    color = CommitLogTheme.colors.textPrimary
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = CommitLogTheme.colors.primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = Dimensions.SpacingMedium)
                    .focusRequester(focusRequester)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.search_back_content_desc),
                    tint = CommitLogTheme.colors.textPrimary
                )
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.search_clear_content_desc),
                        tint = CommitLogTheme.colors.textSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CommitLogTheme.colors.background
        )
    )
}

@Preview(showBackground = true, name = "SearchTopBar - Empty")
@Composable
private fun SearchTopBarEmptyPreview() {
    var query by remember { mutableStateOf("") }
    CommitLogTheme {
        SearchTopBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            onBack = {},
            onClear = { query = "" }
        )
    }
}

@Preview(showBackground = true, name = "SearchTopBar - With Query")
@Composable
private fun SearchTopBarWithQueryPreview() {
    var query by remember { mutableStateOf("Jetpack Compose") }
    CommitLogTheme {
        SearchTopBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            onBack = {},
            onClear = { query = "" }
        )
    }
}
