package com.mh.restaurantchainreservation.feature.wishlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.SubpageCollapsingTopBar
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.components.rememberSubpageCollapsingTopBarScrollBehavior
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonSize
import com.mh.restaurantchainreservation.core.designsystem.components.HeartButtonStyle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistCollection
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.feature.wishlist.ui.GatheredWelcomeModal
import com.mh.restaurantchainreservation.feature.wishlist.ui.ImageGrid
import kotlinx.coroutines.delay

object WishlistRoutes {
    const val Home = "wishlist"
}

@Composable
fun WishlistScreen(
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val collections by WishlistStore.collections.collectAsState()
    val gatheredShown by WishlistStore.gatheredShown.collectAsState()
    val openCollectionId by WishlistStore.openCollectionId.collectAsState()

    var editing by rememberSaveable { mutableStateOf(false) }
    var managingCollections by remember { mutableStateOf(false) }
    var createDialogOpen by remember { mutableStateOf(false) }
    var renameDialog by remember { mutableStateOf<WishlistCollection?>(null) }
    var deleteDialog by remember { mutableStateOf<WishlistCollection?>(null) }
    var showWelcome by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!gatheredShown) {
            delay(150)
            showWelcome = true
        }
    }

    LaunchedEffect(openCollectionId) {
        if (openCollectionId != null) {
            managingCollections = false
        }
    }

    LaunchedEffect(openCollectionId, collections) {
        if (openCollectionId != null && collections.none { it.id == openCollectionId }) {
            WishlistStore.closeOpenCollection()
            editing = false
        }
    }

    val onHome = openCollectionId == null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        AnimatedContent(
            targetState = openCollectionId,
            modifier = Modifier.fillMaxSize(),
            label = "wishlist_screen",
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f),
                    ) + fadeIn(tween(200)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 4 },
                            animationSpec = tween(220),
                        ) + fadeOut(tween(180))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 4 },
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 200f),
                    ) + fadeIn(tween(200)) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(220),
                        ) + fadeOut(tween(180))
                }
            },
            contentKey = { it },
        ) { collectionId ->
            if (collectionId == null) {
                WishlistHomeContent(
                    collections = collections,
                    managingCollections = managingCollections,
                    onNewList = { createDialogOpen = true },
                    onToggleManage = { managingCollections = !managingCollections },
                    onOpenCollection = { col ->
                        WishlistStore.openCollection(col.id)
                        editing = false
                    },
                    onRename = { renameDialog = it },
                    onDelete = { deleteDialog = it },
                )
            } else {
                val current = collections.firstOrNull { it.id == collectionId }
                if (current != null) {
                    WishlistDetailPage(
                        collection = current,
                        editing = editing,
                        onToggleEdit = { editing = !editing },
                        onBack = {
                            WishlistStore.closeOpenCollection()
                            editing = false
                        },
                        onRemove = { rid -> WishlistStore.removeFromAll(rid) },
                        onOpenRestaurant = onOpenRestaurant,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    if (onHome && showWelcome && !gatheredShown) {
        val recent = collections.firstOrNull { it.id == "recent" }?.restaurants
            ?: emptyList()
        val tileImages = if (recent.isEmpty()) {
            DiscoverData.MONTHLY_BEST.take(4).map { it.image }
        } else {
            recent.take(4).map { it.image }
        }
        GatheredWelcomeModal(
            images = tileImages,
            onDismiss = {
                showWelcome = false
                WishlistStore.markGatheredShown()
            },
        )
    }

    if (onHome && createDialogOpen) {
        WishlistNameDialog(
            title = "Create wishlist",
            initialValue = "",
            confirmLabel = "Create",
            onDismiss = { createDialogOpen = false },
            onConfirm = {
                WishlistStore.createCollection(it)
                createDialogOpen = false
            },
        )
    }

    if (onHome) renameDialog?.let { collection ->
        WishlistNameDialog(
            title = "Rename wishlist",
            initialValue = collection.title,
            confirmLabel = "Save",
            onDismiss = { renameDialog = null },
            onConfirm = {
                WishlistStore.renameCollection(collection.id, it)
                renameDialog = null
            },
        )
    }

    if (onHome) deleteDialog?.let { collection ->
        ConfirmDeleteDialog(
            title = collection.title,
            onDismiss = { deleteDialog = null },
            onConfirm = {
                WishlistStore.deleteCollection(collection.id)
                deleteDialog = null
            },
        )
    }
}

@Composable
private fun WishlistHomeContent(
    collections: List<WishlistCollection>,
    managingCollections: Boolean,
    onNewList: () -> Unit,
    onToggleManage: () -> Unit,
    onOpenCollection: (WishlistCollection) -> Unit,
    onRename: (WishlistCollection) -> Unit,
    onDelete: (WishlistCollection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()
    val density = LocalDensity.current
    val collapseRangePx = remember(density) {
        with(density) {
            (CollapsingTitleHeaderMetrics.expandedBodyHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight)
                .toPx()
        }
            .coerceAtLeast(1f)
    }
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf {
            if (gridState.firstVisibleItemIndex == 0) {
                (gridState.firstVisibleItemScrollOffset / collapseRangePx).coerceIn(0f, 1f)
            } else {
                1f
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize().trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(
                    Modifier.height(
                        CollapsingTitleHeaderMetrics.expandedBodyHeight + statusBarTopDp + 8.dp,
                    ),
                )
            }
            items(collections, key = { it.id }) { col ->
                CollectionCard(
                    collection = col,
                    managing = managingCollections,
                    onClick = {
                        if (!managingCollections) {
                            onOpenCollection(col)
                        }
                    },
                    onRename = { onRename(col) },
                    onDelete = { onDelete(col) },
                )
            }
        }

        CollapsingScreenTitleHeader(
            title = "Wishlists",
            collapseProgress = collapseProgress,
            modifier = Modifier.align(Alignment.TopCenter),
            trailing = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ToolbarTextButton(
                        text = "New list",
                        icon = Icons.Outlined.Add,
                        onClick = onNewList,
                    )
                    ToolbarTextButton(
                        text = if (managingCollections) "Done" else "Manage lists",
                        icon = null,
                        onClick = onToggleManage,
                    )
                }
            },
        )
    }
}

@Composable
private fun CollectionCard(
    collection: WishlistCollection,
    managing: Boolean,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(palette.mutedSurface),
        ) {
            ImageGrid(images = collection.restaurants.map { it.image })
            if (managing && !collection.isDefault) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    MiniCircleAction(icon = Icons.Filled.Edit, label = "Rename", onClick = onRename)
                    MiniCircleAction(icon = Icons.Filled.Delete, label = "Delete", destructive = true, onClick = onDelete)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = collection.title,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
        val subtitle = if (collection.id == "recent") "Today" else "${collection.restaurants.size} saved"
        Text(
            text = subtitle,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WishlistDetailPage(
    collection: WishlistCollection,
    editing: Boolean,
    onToggleEdit: () -> Unit,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val scrollBehavior = rememberSubpageCollapsingTopBarScrollBehavior()
    val subtitle = if (collection.id == "recent") {
        "Today"
    } else {
        "${collection.restaurants.size} saved"
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.cardSurface),
    ) {
        SubpageCollapsingTopBar(
            title = collection.title,
            onBack = onBack,
            backContentDescription = "Back",
            scrollBehavior = scrollBehavior,
            actions = {
                Text(
                    text = if (editing) "Done" else "Edit",
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleEdit() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            },
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            items(collection.restaurants, key = { it.id }) { restaurant ->
                DetailItem(
                    restaurant = restaurant,
                    showRemove = editing,
                    onRemove = { onRemove(restaurant.id) },
                    onClick = {
                        if (!editing) onOpenRestaurant(restaurant.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    restaurant: Restaurant,
    showRemove: Boolean,
    onRemove: () -> Unit,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !showRemove, onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(palette.mutedSurface),
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            if (showRemove) {
                HeartButton(
                    active = true,
                    onClick = onRemove,
                    size = HeartButtonSize.Medium,
                    style = HeartButtonStyle.Overlay,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = restaurant.name,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = restaurant.cuisine,
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false),
            )
            Spacer(Modifier.size(6.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(11.dp),
            )
            Spacer(Modifier.size(2.dp))
            Text(
                text = "%.1f".format(restaurant.rating),
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ToolbarTextButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val pillShape = RoundedCornerShape(percent = 50)
    val pillBackground = if (palette.isDark) palette.mutedSurface else palette.cardSurface
    Row(
        modifier = Modifier
            .heightIn(min = 38.dp)
            .clip(pillShape)
            .background(pillBackground, pillShape)
            .border(1.dp, palette.border, pillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(16.dp))
        }
        Text(text, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MiniCircleAction(
    icon: ImageVector,
    label: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.95f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (destructive) palette.destructive else palette.foreground,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun WishlistNameDialog(
    title: String,
    initialValue: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var name by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.cardSurface)
                    .clickable {}
                    .padding(20.dp),
            ) {
                Text(title, color = palette.foreground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { if (it.length <= 50) name = it },
                        singleLine = true,
                        cursorBrush = SolidColor(palette.foreground),
                        textStyle = TextStyle(color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Medium),
                        decorationBox = { inner ->
                            if (name.isEmpty()) Text("Name", color = palette.mutedForeground, fontSize = 16.sp)
                            inner()
                        },
                    )
                }
                Text("${name.length}/50 characters", color = palette.mutedForeground, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                val canCreate = name.trim().isNotEmpty()
                val actionShape = RoundedCornerShape(14.dp)
                val actionHeight = 50.dp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(actionHeight)
                            .clip(actionShape)
                            .border(1.dp, palette.border, actionShape)
                            .background(palette.cardSurface)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Cancel",
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(actionHeight)
                            .clip(actionShape)
                            .background(if (canCreate) palette.foreground else palette.mutedSurface)
                            .clickable(enabled = canCreate) { onConfirm(name.trim()) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            confirmLabel,
                            color = if (canCreate) palette.cardSurface else palette.mutedForeground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.cardSurface)
                    .clickable {}
                    .padding(20.dp),
            ) {
                Text("Delete wishlist?", color = palette.foreground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Restaurants saved in \"$title\" will move back to Recently searched restaurants.",
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        "Cancel",
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.destructive)
                            .clickable(onClick = onConfirm)
                            .padding(horizontal = 22.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Delete", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
