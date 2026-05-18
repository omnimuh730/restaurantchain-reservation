package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.HeartDrawableIcon
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

/**
 * Renders 0/1/2/3/4+ image tiles inside a square cover, mirroring the React
 * `ImageGrid` used by the Wishlist sheet covers and the Wishlist list cards.
 *
 * The container fills its parent — callers supply the aspect-ratio'd Box.
 */
@Composable
fun ImageGrid(
    images: List<String>,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.mutedSurface),
        contentAlignment = Alignment.Center,
    ) {
        when {
            images.isEmpty() -> {
                HeartDrawableIcon(
                    active = false,
                    contentDescription = null,
                    iconHeight = 36.dp,
                )
            }
            images.size == 1 -> {
                AsyncImage(
                    model = images[0],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            images.size == 2 -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    AsyncImage(
                        model = images[0],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    )
                    AsyncImage(
                        model = images[1],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    )
                }
            }
            images.size == 3 -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    AsyncImage(
                        model = images[0],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    )
                    Column(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        AsyncImage(
                            model = images[1],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                        AsyncImage(
                            model = images[2],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                    }
                }
            }
            else -> {
                // 4+ images: 2x2 grid using the first 4.
                val first = images[0]
                val second = images[1]
                val third = images[2]
                val fourth = images[3]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        AsyncImage(
                            model = first,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.weight(1f).fillMaxSize(),
                        )
                        AsyncImage(
                            model = second,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.weight(1f).fillMaxSize(),
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        AsyncImage(
                            model = third,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.weight(1f).fillMaxSize(),
                        )
                        AsyncImage(
                            model = fourth,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.weight(1f).fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
