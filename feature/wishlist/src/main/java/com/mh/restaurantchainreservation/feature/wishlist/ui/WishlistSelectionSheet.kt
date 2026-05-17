package com.mh.restaurantchainreservation.feature.wishlist.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.components.ModalSheetDragHandle
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistCollection
import com.mh.restaurantchainreservation.core.model.WishlistStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class SheetView { Select, Create }

/**
 * The Airbnb-style "Save to wishlist" sheet. Slides up from the bottom, has
 * two switchable views: a 2-col grid of collections (with a save animation
 * overlay on a tapped cover), and a "Create wishlist" form.
 */
@Composable
fun WishlistSelectionSheet(
    restaurant: Restaurant,
    onDismiss: () -> Unit,
) {
    val collections by WishlistStore.collections.collectAsState()
    var view by remember { mutableStateOf(SheetView.Select) }
    var savingCollectionId by remember { mutableStateOf<String?>(null) }

    val backdropAlpha = remember { Animatable(0f) }
    val sheetOffset = remember { Animatable(1f) }
    val sheetScale = remember { Animatable(0.98f) }
    val scope = rememberCoroutineScope()
    var dismissing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch { backdropAlpha.animateTo(1f, tween(220, easing = LinearOutSlowInEasing)) }
        scope.launch {
            sheetOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 270f),
            )
        }
        scope.launch {
            sheetScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 270f),
            )
        }
    }

    val triggerDismiss: () -> Unit = remember {
        {
            if (!dismissing) {
                dismissing = true
                scope.launch {
                    val a = launch { backdropAlpha.animateTo(0f, tween(180)) }
                    val b = launch { sheetOffset.animateTo(1f, tween(220)) }
                    a.join(); b.join()
                    onDismiss()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = triggerDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Backdrop.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backdropAlpha.value * 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    ) { triggerDismiss() },
            )

            // Sheet container, anchored to the bottom.
            val density = LocalDensity.current
            // Convert offset (0..1) into pixels approximating sheet height (88vh).
            val sheetTranslateY = with(density) { (sheetOffset.value * 800.dp.toPx()) }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .heightIn(max = 720.dp)
                    .graphicsLayer {
                        translationY = sheetTranslateY
                        scaleX = sheetScale.value
                        scaleY = sheetScale.value
                    },
            ) {
                SheetSurface(
                    view = view,
                    collections = collections,
                    restaurant = restaurant,
                    savingCollectionId = savingCollectionId,
                    onTapCover = { collectionId ->
                        if (savingCollectionId != null) return@SheetSurface
                        savingCollectionId = collectionId
                        scope.launch {
                            delay(280)
                            WishlistStore.saveTo(collectionId, restaurant)
                            triggerDismiss()
                        }
                    },
                    onCreateNew = { view = SheetView.Create },
                    onBackToSelect = { view = SheetView.Select },
                    onClose = triggerDismiss,
                    onSubmitCreate = { name ->
                        WishlistStore.createCollectionAndSave(name, restaurant)
                        triggerDismiss()
                    },
                )
            }
        }
    }
}

@Composable
private fun SheetSurface(
    view: SheetView,
    collections: List<WishlistCollection>,
    restaurant: Restaurant,
    savingCollectionId: String?,
    onTapCover: (String) -> Unit,
    onCreateNew: () -> Unit,
    onBackToSelect: () -> Unit,
    onClose: () -> Unit,
    onSubmitCreate: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(palette.cardSurface)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        ModalSheetDragHandle(width = 36.dp)
        AnimatedContent(
            targetState = view,
            label = "wishlist-sheet-view",
            transitionSpec = {
                if (targetState == SheetView.Create) {
                    (slideInHorizontally(tween(220)) { it / 4 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(220)) { -it / 4 } + fadeOut(tween(180)))
                } else {
                    (slideInHorizontally(tween(220)) { -it / 4 } + fadeIn(tween(220))) togetherWith
                        (slideOutHorizontally(tween(220)) { it / 4 } + fadeOut(tween(180)))
                }
            },
        ) { targetView ->
            when (targetView) {
                SheetView.Select -> SelectView(
                    collections = collections,
                    restaurant = restaurant,
                    savingCollectionId = savingCollectionId,
                    onTapCover = onTapCover,
                    onCreateNew = onCreateNew,
                    onClose = onClose,
                )
                SheetView.Create -> CreateView(
                    onBack = onBackToSelect,
                    onSubmit = onSubmitCreate,
                )
            }
        }
    }
}

@Composable
private fun SelectView(
    collections: List<WishlistCollection>,
    restaurant: Restaurant,
    savingCollectionId: String?,
    onTapCover: (String) -> Unit,
    onCreateNew: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Save to wishlist",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = palette.foreground,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        // Grid of covers.
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(collections, key = { it.id }) { col ->
                CollectionCoverButton(
                    collection = col,
                    isSaving = savingCollectionId == col.id,
                    isCurrentlySavedHere = col.restaurants.any { it.id == restaurant.id },
                    onTap = { onTapCover(col.id) },
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Create new wishlist pill.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.foreground)
                    .clickable { onCreateNew() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = palette.cardSurface,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Create new wishlist",
                    color = palette.cardSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

@Composable
private fun CollectionCoverButton(
    collection: WishlistCollection,
    isSaving: Boolean,
    isCurrentlySavedHere: Boolean,
    onTap: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val pressedScale = remember { Animatable(1f) }
    LaunchedEffect(isSaving) {
        if (isSaving) {
            pressedScale.animateTo(0.985f, tween(120))
        } else {
            pressedScale.animateTo(1f, tween(150))
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = pressedScale.value
                    scaleY = pressedScale.value
                }
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(palette.mutedSurface)
                .clickable(enabled = !isSaving) { onTap() },
        ) {
            ImageGrid(images = collection.restaurants.map { it.image })
            // Saved-here indicator (small heart top-right) when this collection
            // currently holds the restaurant — helps the user "move" out.
            if (isCurrentlySavedHere && !isSaving) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            // Saving overlay (animated check + sparkles). The overlay owns
            // its own enter animation via Animatable, so a plain conditional
            // mount is enough — avoids overload-resolution ambiguity between
            // the ColumnScope and top-level AnimatedVisibility variants.
            if (isSaving) {
                SavingOverlay()
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = collection.title,
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        Text(
            text = "${collection.restaurants.size} saved",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun SavingOverlay() {
    val palette = LocalRestaurantPalette.current
    val scale = remember { Animatable(0.4f) }
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0.4f at 0
                    1.16f at 200
                    1f at 350
                },
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    10f at 175
                    0f at 350
                },
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center,
    ) {
        // Sparkles top-right.
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(20.dp),
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .scale(scale.value)
                .graphicsLayer { rotationZ = rotation.value }
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
private fun CreateView(
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var name by remember { mutableStateOf("") }
    val maxLength = 50
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Brief delay so the slide-in animation finishes before the keyboard
        // shoves the layout — keeps the entrance smooth.
        delay(180)
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        // Header with back arrow + centered title.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "Create wishlist",
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        // Input field.
        val borderColor = if (name.isNotEmpty()) palette.foreground else palette.border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = name,
                onValueChange = { value ->
                    if (value.length <= maxLength) name = value
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (name.trim().isNotEmpty()) onSubmit(name.trim())
                }),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(palette.foreground),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = { inner ->
                    if (name.isEmpty()) {
                        Text(
                            text = "Name",
                            color = palette.mutedForeground,
                            fontSize = 16.sp,
                        )
                    }
                    inner()
                },
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${name.length}/$maxLength characters",
            color = palette.mutedForeground,
            fontSize = 12.sp,
        )
        Spacer(Modifier.height(20.dp))
        // Footer (Cancel + Create).
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Cancel",
                color = palette.foreground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            )
            val enabled = name.trim().isNotEmpty()
            val bg = if (enabled) palette.foreground else palette.mutedSurface
            val fg = if (enabled) palette.cardSurface else palette.mutedForeground
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(enabled = enabled) { onSubmit(name.trim()) }
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Create",
                    color = fg,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
