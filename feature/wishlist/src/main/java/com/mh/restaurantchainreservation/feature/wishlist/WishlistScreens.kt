package com.mh.restaurantchainreservation.feature.wishlist

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.designsystem.tokens.pageCanvasBackground
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingScreenTitleHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageHeaderIconButton
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageScreenHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.collapsingHeaderGridScroll
import com.mh.restaurantchainreservation.core.designsystem.components.collapsingHeaderListScroll
import com.mh.restaurantchainreservation.core.designsystem.components.rememberCollapsingHeaderScrollState
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.SharedContentStore
import com.mh.restaurantchainreservation.core.model.SharedFolder
import com.mh.restaurantchainreservation.core.model.SharedInboxEntry
import com.mh.restaurantchainreservation.core.model.SharedItemKind
import com.mh.restaurantchainreservation.core.model.WishlistCollection
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.feature.wishlist.ui.GatheredWelcomeModal
import com.mh.restaurantchainreservation.feature.wishlist.ui.ImageGrid
import com.mh.restaurantchainreservation.feature.wishlist.ui.RecentlyViewedGridItem
import com.mh.restaurantchainreservation.feature.wishlist.ui.WishlistRestaurantResultCard
import com.mh.restaurantchainreservation.feature.wishlist.ui.WishlistSettingsSheet
import com.mh.restaurantchainreservation.feature.wishlist.ui.WishlistShareFriendsSheet
import com.mh.restaurantchainreservation.core.model.groupRecentlyViewedByDay
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
    val sharedFolders by SharedContentStore.sharedFolders.collectAsState()
    val gatheredShown by WishlistStore.gatheredShown.collectAsState()
    val openCollectionId by WishlistStore.openCollectionId.collectAsState()
    var openSharedFolder by remember { mutableStateOf<SharedFolder?>(null) }

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
            if (openCollectionId != WishlistStore.RECENT_COLLECTION_ID) {
                editing = false
            }
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
            .background(palette.pageBackground),
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
                    sharedFolders = sharedFolders,
                    managingCollections = managingCollections,
                    onNewList = { createDialogOpen = true },
                    onToggleManage = { managingCollections = !managingCollections },
                    onOpenCollection = { col ->
                        WishlistStore.openCollection(col.id)
                        editing = false
                    },
                    onOpenSharedFolder = { openSharedFolder = it },
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
                        onOpenRestaurant = onOpenRestaurant,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        if (onHome) {
            openSharedFolder?.let { folder ->
                SharedFolderDetailPage(
                    folder = folder,
                    onBack = { openSharedFolder = null },
                    onOpenRestaurant = onOpenRestaurant,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(3f),
                )
            }
        }
    }

    if (onHome && showWelcome && !gatheredShown) {
        val recent = collections.firstOrNull { it.id == WishlistStore.RECENT_COLLECTION_ID }
        val tileImages = recent?.displayRestaurants()?.take(4)?.map { it.image }
            ?: DiscoverData.MONTHLY_BEST.take(4).map { it.image }
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
                if (WishlistStore.createCollection(it)) {
                    createDialogOpen = false
                }
            },
            nameTaken = { WishlistStore.isWishlistNameTaken(it) },
        )
    }

    if (onHome) renameDialog?.let { collection ->
        WishlistNameDialog(
            title = "Rename wishlist",
            initialValue = collection.title,
            confirmLabel = "Save",
            onDismiss = { renameDialog = null },
            onConfirm = {
                if (WishlistStore.renameCollection(collection.id, it)) {
                    renameDialog = null
                }
            },
            nameTaken = { WishlistStore.isWishlistNameTaken(it, excludingCollectionId = collection.id) },
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
    sharedFolders: List<SharedFolder>,
    managingCollections: Boolean,
    onNewList: () -> Unit,
    onToggleManage: () -> Unit,
    onOpenCollection: (WishlistCollection) -> Unit,
    onOpenSharedFolder: (SharedFolder) -> Unit,
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
    val headerScroll = rememberCollapsingHeaderScrollState(collapseRangePx)
    headerScroll.BindGridResetOnShortContent(gridState)
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf { headerScroll.collapseProgress(gridState) }
    }
    val topContentInset by remember {
        derivedStateOf {
            CollapsingTitleHeaderMetrics.collapsingTopContentInset(
                collapseProgress = collapseProgress,
                expandedBodyHeight = CollapsingTitleHeaderMetrics.expandedBodyHeight,
                statusBarTopDp = statusBarTopDp,
                firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = gridState.firstVisibleItemScrollOffset,
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .collapsingHeaderGridScroll(headerScroll, gridState)
                .trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(topContentInset))
            }
            if (sharedFolders.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SharedWithYouSectionHeader()
                }
                items(
                    sharedFolders,
                    key = { it.sharerId },
                    span = { GridItemSpan(maxLineSpan) },
                ) { folder ->
                    SharedFolderCard(
                        folder = folder,
                        onClick = { onOpenSharedFolder(folder) },
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    MyWishlistsSectionHeader()
                }
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
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
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
            ImageGrid(images = collection.displayRestaurants().map { it.image })
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
        val subtitle = if (collection.id == WishlistStore.RECENT_COLLECTION_ID) {
            recentlyViewedSubtitle(collection)
        } else {
            "${collection.itemCount()} saved"
        }
        Text(
            text = subtitle,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun WishlistDetailPage(
    collection: WishlistCollection,
    editing: Boolean,
    onToggleEdit: () -> Unit,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (collection.isDefault) {
        RecentlyViewedDetailPage(
            collection = collection,
            editing = editing,
            onToggleEdit = onToggleEdit,
            onBack = onBack,
            onOpenRestaurant = onOpenRestaurant,
            modifier = modifier,
        )
    } else {
        UserWishlistDetailPage(
            collection = collection,
            onBack = {
                WishlistStore.flushUnsavedInCollection(collection.id)
                onBack()
            },
            onOpenRestaurant = onOpenRestaurant,
            modifier = modifier,
        )
    }
}

@Composable
private fun RecentlyViewedDetailPage(
    collection: WishlistCollection,
    editing: Boolean,
    onToggleEdit: () -> Unit,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val gridState = rememberLazyGridState()
    val density = LocalDensity.current
    val headerExpandedHeight = CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(hasSubtitle = false)
    val collapseRangePx = remember(density, headerExpandedHeight) {
        with(density) {
            (headerExpandedHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight).toPx()
        }.coerceAtLeast(1f)
    }
    val headerScroll = rememberCollapsingHeaderScrollState(collapseRangePx)
    headerScroll.BindGridResetOnShortContent(gridState)
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf { headerScroll.collapseProgress(gridState) }
    }
    val topContentInset by remember {
        derivedStateOf {
            CollapsingTitleHeaderMetrics.collapsingTopContentInset(
                collapseProgress = collapseProgress,
                expandedBodyHeight = headerExpandedHeight,
                statusBarTopDp = statusBarTopDp,
                firstVisibleItemIndex = gridState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = gridState.firstVisibleItemScrollOffset,
            )
        }
    }
    val dayGroups = remember(collection.recentlyViewed) {
        groupRecentlyViewedByDay(collection.recentlyViewed)
    }
    val itemPlacementSpec = spring<IntOffset>(
        stiffness = Spring.StiffnessMediumLow,
        dampingRatio = Spring.DampingRatioNoBouncy,
    )
    val itemFadeSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .collapsingHeaderGridScroll(headerScroll, gridState)
                .trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(Modifier.height(topContentInset))
            }
            dayGroups.forEach { group ->
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "header-${group.dayLabel}",
                ) {
                    Text(
                        text = group.dayLabel,
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 12.dp)
                            .animateItem(
                                fadeInSpec = itemFadeSpec,
                                fadeOutSpec = itemFadeSpec,
                                placementSpec = itemPlacementSpec,
                            ),
                    )
                }
                items(group.entries, key = { it.restaurant.id }) { entry ->
                    RecentlyViewedGridItem(
                        restaurant = entry.restaurant,
                        editing = editing,
                        onOpen = { onOpenRestaurant(entry.restaurant.id) },
                        onRemoveFromRecentlyViewed = {
                            WishlistStore.removeFromRecentlyViewed(entry.restaurant.id)
                        },
                        onHeartTap = { WishlistStore.onHeartTapInRecentlyViewed(entry.restaurant) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = itemFadeSpec,
                            fadeOutSpec = itemFadeSpec,
                            placementSpec = itemPlacementSpec,
                        ),
                    )
                }
            }
        }

        CollapsingSubpageScreenHeader(
            title = collection.title,
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = "Back",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
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
    }
}

private fun recentlyViewedSubtitle(collection: WishlistCollection): String {
    val groups = groupRecentlyViewedByDay(collection.recentlyViewed)
    return when {
        groups.isEmpty() -> "No history yet"
        groups.size == 1 -> groups.first().dayLabel
        else -> "${collection.itemCount()} places"
    }
}

@Composable
private fun UserWishlistDetailPage(
    collection: WishlistCollection,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val subtitle = "${collection.itemCount()} saved"
    val headerExpandedHeight = CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(hasSubtitle = true)
    val collapseRangePx = remember(density, headerExpandedHeight) {
        with(density) {
            (headerExpandedHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight).toPx()
        }.coerceAtLeast(1f)
    }
    val headerScroll = rememberCollapsingHeaderScrollState(collapseRangePx)
    headerScroll.BindListResetOnShortContent(listState)
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf { headerScroll.collapseProgress(listState) }
    }
    val topContentInset by remember {
        derivedStateOf {
            CollapsingTitleHeaderMetrics.collapsingTopContentInset(
                collapseProgress = collapseProgress,
                expandedBodyHeight = headerExpandedHeight,
                statusBarTopDp = statusBarTopDp,
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
            )
        }
    }
    var showSettings by remember { mutableStateOf(false) }
    var showShareFriends by remember { mutableStateOf(false) }
    var renameDialogOpen by remember { mutableStateOf(false) }
    var deleteCollectionDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .collapsingHeaderListScroll(headerScroll, listState)
                .trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(topContentInset))
            }
            items(collection.restaurants, key = { it.id }) { restaurant ->
                WishlistRestaurantResultCard(
                    restaurant = restaurant,
                    onClick = { onOpenRestaurant(restaurant.id) },
                    onHeartTap = {
                        if (WishlistStore.isSaved(restaurant.id)) {
                            WishlistStore.unsaveInCollectionKeepingInList(collection.id, restaurant)
                        } else {
                            WishlistStore.saveToCollection(collection.id, restaurant)
                        }
                    },
                )
            }
        }

        CollapsingSubpageScreenHeader(
            title = collection.title,
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = "Back",
            subtitle = subtitle,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
            actions = { progress ->
                CollapsingSubpageHeaderIconButton(
                    collapseProgress = progress,
                    onClick = { showShareFriends = true },
                    contentDescription = "Share wishlist",
                    imageVector = Icons.Outlined.IosShare,
                )
                CollapsingSubpageHeaderIconButton(
                    collapseProgress = progress,
                    onClick = { showSettings = true },
                    contentDescription = "Wishlist options",
                    imageVector = Icons.Outlined.MoreVert,
                )
            },
        )
    }

    if (showSettings) {
        WishlistSettingsSheet(
            onDismiss = { showSettings = false },
            onShare = { showShareFriends = true },
            onRename = { renameDialogOpen = true },
            onDelete = { deleteCollectionDialog = true },
        )
    }

    if (showShareFriends) {
        WishlistShareFriendsSheet(
            collection = collection,
            onDismiss = { showShareFriends = false },
        )
    }

    if (renameDialogOpen) {
        WishlistNameDialog(
            title = "Rename wishlist",
            initialValue = collection.title,
            confirmLabel = "Save",
            onDismiss = { renameDialogOpen = false },
            onConfirm = {
                if (WishlistStore.renameCollection(collection.id, it)) {
                    renameDialogOpen = false
                }
            },
            nameTaken = { WishlistStore.isWishlistNameTaken(it, excludingCollectionId = collection.id) },
        )
    }

    if (deleteCollectionDialog) {
        ConfirmDeleteDialog(
            title = collection.title,
            onDismiss = { deleteCollectionDialog = false },
            onConfirm = {
                WishlistStore.deleteCollection(collection.id)
                deleteCollectionDialog = false
                onBack()
            },
        )
    }

    androidx.compose.runtime.DisposableEffect(collection.id) {
        onDispose {
            WishlistStore.flushUnsavedInCollection(collection.id)
        }
    }
}

@Composable
private fun ConfirmRemoveFromWishlistDialog(
    collectionTitle: String,
    restaurantName: String,
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
                .background(RestaurantColors.Overlay.scrimMedium)
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
                Text(
                    text = "Remove from wishlist?",
                    color = palette.foreground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "\"$restaurantName\" will be removed from \"$collectionTitle\".",
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
                        text = "Cancel",
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
                        Text(
                            text = "Remove",
                            color = RestaurantColors.Base.white,
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
private fun ToolbarTextButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val pillShape = RoundedCornerShape(percent = 50)
    val pillBackground = palette.cardSurface
    Row(
        modifier = Modifier
            .heightIn(min = 32.dp)
            .clip(pillShape)
            .background(pillBackground, pillShape)
            .border(1.dp, palette.border, pillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(14.dp))
        }
        Text(text, color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
            .background(RestaurantColors.Overlay.veilFrosted)
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
    nameTaken: (String) -> Boolean = { false },
) {
    val palette = LocalRestaurantPalette.current
    var name by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    val trimmed = name.trim()
    val duplicate = trimmed.isNotEmpty() && nameTaken(trimmed)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RestaurantColors.Overlay.scrimMedium)
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
                if (duplicate) {
                    Text(
                        text = "You already have a wishlist with this name.",
                        color = palette.destructive,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                val canCreate = trimmed.isNotEmpty() && !duplicate
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
                .background(RestaurantColors.Overlay.scrimMedium)
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
                    "Restaurants saved in \"$title\" will move back to Recently Viewed.",
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
                        Text("Delete", color = RestaurantColors.Base.white, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedWithYouSectionHeader() {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Shared with you",
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Restaurants and wishlists from your contacts",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
        )
    }
}

@Composable
private fun MyWishlistsSectionHeader() {
    val palette = LocalRestaurantPalette.current
    Text(
        text = "My wishlists",
        color = palette.foreground,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun SharedFolderCard(
    folder: SharedFolder,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(2.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(palette.mutedSurface),
        ) {
            val images = folder.entries.flatMap { it.previewImages() }.take(4)
            if (images.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.IosShare,
                        contentDescription = null,
                        tint = palette.mutedForeground,
                        modifier = Modifier.size(32.dp),
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    images.forEach { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = folder.folderTitle,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = folder.summary,
            color = palette.mutedForeground,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun SharedFolderDetailPage(
    folder: SharedFolder,
    onBack: () -> Unit,
    onOpenRestaurant: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val subtitle = folder.summary
    val headerExpandedHeight = CollapsingTitleHeaderMetrics.subpageExpandedBodyHeight(hasSubtitle = true)
    val collapseRangePx = remember(density, headerExpandedHeight) {
        with(density) {
            (headerExpandedHeight - CollapsingTitleHeaderMetrics.collapsedBodyHeight).toPx()
        }.coerceAtLeast(1f)
    }
    val headerScroll = rememberCollapsingHeaderScrollState(collapseRangePx)
    headerScroll.BindListResetOnShortContent(listState)
    val statusBarTopDp = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val collapseProgress by remember {
        derivedStateOf { headerScroll.collapseProgress(listState) }
    }
    val topContentInset by remember {
        derivedStateOf {
            CollapsingTitleHeaderMetrics.collapsingTopContentInset(
                collapseProgress = collapseProgress,
                expandedBodyHeight = headerExpandedHeight,
                statusBarTopDp = statusBarTopDp,
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .collapsingHeaderListScroll(headerScroll, listState)
                .trackBottomNavScroll(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.height(topContentInset))
            }
            items(folder.entries, key = { it.id }) { entry ->
                when (entry.kind) {
                    SharedItemKind.Restaurant -> {
                        val restaurant = entry.restaurant
                        if (restaurant != null) {
                            WishlistRestaurantResultCard(
                                restaurant = restaurant,
                                onClick = { onOpenRestaurant(restaurant.id) },
                                onHeartTap = { WishlistStore.onHeartTap(restaurant) },
                            )
                        }
                    }
                    SharedItemKind.Wishlist -> {
                        SharedWishlistEntryCard(
                            entry = entry,
                            onOpenRestaurant = onOpenRestaurant,
                        )
                    }
                }
            }
        }

        CollapsingSubpageScreenHeader(
            title = folder.folderTitle,
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = "Back",
            subtitle = subtitle,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
        )
    }
}

@Composable
private fun SharedWishlistEntryCard(
    entry: SharedInboxEntry,
    onOpenRestaurant: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val wishlist = entry.wishlist ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.IosShare,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wishlist.title,
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = entry.displaySubtitle,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                )
            }
        }
        wishlist.restaurants.take(3).forEach { restaurant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenRestaurant(restaurant.id) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AsyncImage(
                    model = restaurant.image,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        color = palette.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = restaurant.cuisine,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                }
            }
        }
        if (wishlist.restaurants.size > 3) {
            Text(
                text = "+ ${wishlist.restaurants.size - 3} more",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
