package com.anynetwork.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anynetwork.app.ui.components.button.BackButton
import com.anynetwork.app.ui.components.text.Header


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    topBar: ToolbarState = ToolbarState.Hidden,
    hexagonGrid: @Composable (() -> Unit)? = null,
    applyInnerPaddingToContent: Boolean = true,
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentColor = containerColor,
        containerColor = containerColor,
        topBar = {
            if (topBar is ToolbarState.Shown) CenterAlignedTopAppBar(
                modifier = topBar.modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val titleState = topBar.titleState) {
                            is ToolbarStateTitle.Text -> {
                                Header(
                                    text = titleState.text
                                )
                            }

                            is ToolbarStateTitle.Custom -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    titleState.content?.invoke(this)
                                }
                            }

                            null -> {}
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            when (val navigationIconState = topBar.navigationIconState) {
                                is NavigationIconState.BackButton -> {
                                    BackButton {
                                        navigationIconState.onBackButtonClick.invoke()
                                    }
                                }
                                is NavigationIconState.Custom -> {
                                    Row {
                                        navigationIconState.content?.invoke(this)
                                    }
                                }
                                null -> {}
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        Box(contentAlignment = Alignment.CenterEnd) {
                            topBar.actions?.invoke(this)
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
                hexagonGrid?.invoke()
                Box(
                    modifier = Modifier.apply {
                        if (applyInnerPaddingToContent) padding(innerPadding)
                    }
                ) {
                    content?.invoke(this)
                }
        }
    )
}

sealed class ToolbarState {
    object Hidden: ToolbarState()
    data class Shown(
        val modifier: Modifier = Modifier,
        val navigationIconState: NavigationIconState? = null,
        val titleState: ToolbarStateTitle? = null,
        val actions: @Composable (BoxScope.() -> Unit)? = null
    ): ToolbarState()
}

sealed class NavigationIconState {
    data class BackButton(val onBackButtonClick: (() -> Unit)): NavigationIconState()
    data class Custom(val content: @Composable (RowScope.() -> Unit)? = null): NavigationIconState()
}

sealed class ToolbarStateTitle {
    data class Text(val text: String): ToolbarStateTitle()
    data class Custom(val content: @Composable (RowScope.() -> Unit)? = null): ToolbarStateTitle()
}