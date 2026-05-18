package com.mh.restaurantchainreservation.feature.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.NewsCategory
import com.mh.restaurantchainreservation.core.model.NewsData
import com.mh.restaurantchainreservation.core.model.NewsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val NewsCardShape = RoundedCornerShape(16.dp)
private val DetailSheetTopRadius = 34.dp

@Composable
fun NewsListScreen(
    onBack: () -> Unit,
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var items by remember { mutableStateOf(NewsData.all) }
    var loading by remember { mutableStateOf(false) }
    var visibleCount by remember { mutableIntStateOf(4) }
    var loadingMore by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val visible = items.take(visibleCount)
    val hasMore = visibleCount < items.size

    fun refresh() {
        loading = true
        scope.launch {
            delay(800L)
            items = NewsData.all
            visibleCount = 4
            loading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        NewsListHeader(
            onBack = onBack,
            onRefresh = { refresh() },
            refreshing = loading,
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { article ->
                NewsListCard(
                    article = article,
                    onClick = { onOpenArticle(article.id) },
                )
            }
            if (hasMore) {
                item(key = "load-more") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (loadingMore) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.dp,
                                color = palette.foreground.copy(alpha = 0.55f),
                            )
                        } else {
                            NewsLoadMoreButton(
                                onClick = {
                                    loadingMore = true
                                    scope.launch {
                                        delay(600L)
                                        visibleCount = (visibleCount + 4).coerceAtMost(items.size)
                                        loadingMore = false
                                    }
                                },
                            )
                        }
                    }
                }
            } else if (items.size > 4) {
                item(key = "end") {
                    Text(
                        text = "You've reached the end",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun NewsDetailScreen(
    articleId: String,
    onBack: () -> Unit,
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val article = remember(articleId) { NewsData.findById(articleId) }
    if (article == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val related = remember(articleId) {
        NewsData.all.filter { it.id != articleId }.take(3)
    }
    val scroll = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(288.dp),
            ) {
                AsyncImage(
                    model = article.image,
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.12f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.42f),
                                ),
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = 16.dp, top = 12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.88f))
                        .clickable(role = Role.Button, onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = palette.foreground,
                        modifier = Modifier.size(22.dp),
                    )
                }
                NewsCategoryBadge(
                    category = article.category,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(end = 16.dp, top = 12.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = DetailSheetTopRadius, topEnd = DetailSheetTopRadius))
                    .background(palette.cardSurface)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                Text(
                    text = article.title,
                    color = palette.foreground,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AsyncImage(
                        model = article.authorAvatar,
                        contentDescription = article.author,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = article.author,
                            color = palette.foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = palette.mutedForeground,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = "${formatNewsTimeAgo(article.publishedAtEpochMs)} · ${article.readMinutes} mins read",
                                color = palette.mutedForeground,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
                Text(
                    text = article.summary,
                    color = palette.mutedForeground,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = article.body,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    article.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            color = palette.foreground,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(palette.mutedSurface)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }

                if (related.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 32.dp, bottom = 20.dp),
                        color = palette.borderSoft,
                    )
                    Text(
                        text = "More Stories",
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    related.forEach { relatedItem ->
                        NewsRelatedRow(
                            article = relatedItem,
                            onClick = { onOpenArticle(relatedItem.id) },
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                }
                Spacer(
                    Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .height(24.dp),
                )
            }
        }
    }
}

@Composable
private fun NewsListHeader(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    refreshing: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = palette.foreground,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = "News & Stories",
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(enabled = !refreshing, role = Role.Button, onClick = onRefresh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = "Refresh",
                tint = palette.foreground,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = if (refreshing) 360f else 0f },
            )
        }
    }
}

@Composable
private fun NewsListCard(
    article: NewsItem,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(NewsCardShape)
            .border(1.dp, palette.border, NewsCardShape)
            .background(palette.cardSurface)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp),
        ) {
            AsyncImage(
                model = article.image,
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f)),
                        ),
                    ),
            )
            NewsCategoryBadge(
                category = article.category,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = formatNewsTimeAgo(article.publishedAtEpochMs),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("·", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                Text(
                    text = "${article.readMinutes} mins read",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = article.title,
                color = palette.foreground,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = article.summary,
                color = palette.mutedForeground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = article.authorAvatar,
                    contentDescription = article.author,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape),
                )
                Text(
                    text = article.author,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun NewsRelatedRow(
    article: NewsItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = article.image,
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = article.title,
                color = palette.foreground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${article.category.displayLabel()} · ${formatNewsTimeAgo(article.publishedAtEpochMs)}",
                color = palette.mutedForeground,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun NewsCategoryBadge(
    category: NewsCategory,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val bg = category.badgeColor(palette)
    Text(
        text = category.displayLabel(),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 4.dp),
    )
}

@Composable
private fun NewsLoadMoreButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
            .background(palette.cardSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = palette.foreground,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer { rotationZ = 90f },
        )
        Text(
            text = "Show more",
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
