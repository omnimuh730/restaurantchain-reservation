package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Material [LargeTopAppBar] + [TopAppBarDefaults.exitUntilCollapsedScrollBehavior], matching
 * wishlist collection detail (e.g. Recently Viewed).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSubpageCollapsingTopBarScrollBehavior(): TopAppBarScrollBehavior {
    val topAppBarState = rememberTopAppBarState()
    return TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubpageCollapsingTopBar(
    title: String,
    onBack: () -> Unit,
    backContentDescription: String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val palette = LocalRestaurantPalette.current
    Box(modifier = modifier.fillMaxWidth()) {
        LargeTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = palette.foreground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 0.dp),
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = backContentDescription,
                        tint = palette.foreground,
                    )
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = palette.pageBackground,
                scrolledContainerColor = palette.pageBackground,
                titleContentColor = palette.foreground,
                actionIconContentColor = palette.foreground,
                navigationIconContentColor = palette.foreground,
            ),
            scrollBehavior = scrollBehavior,
        )
    }
}
