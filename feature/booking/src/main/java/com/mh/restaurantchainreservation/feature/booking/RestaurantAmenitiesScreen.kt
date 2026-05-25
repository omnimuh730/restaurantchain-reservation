package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.SignalCellularAlt
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EventSeat
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.RoomService
import androidx.compose.material.icons.outlined.TakeoutDining
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material.icons.outlined.WheelchairPickup
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageScreenHeader
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingTitleHeaderMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.collapsingHeaderListScroll
import com.mh.restaurantchainreservation.core.designsystem.components.rememberCollapsingHeaderScrollState
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.pageCanvasBackground
import com.mh.restaurantchainreservation.core.model.Restaurant

@Composable
fun RestaurantAmenitiesScreen(
    restaurant: Restaurant,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val ext = remember(restaurant) { RestaurantDetailData.extendedData(restaurant) }
    val chipCategories = remember(ext) { RestaurantAmenitiesData.placeOfferChipCategories(ext) }
    val listCategories = remember(restaurant, ext) {
        RestaurantAmenitiesData.extendedCategories(restaurant, ext)
    }

    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val slideProgress by animateFloatAsState(
        targetValue = if (entered) 0f else 1f,
        animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing),
        label = "amenitiesSlide",
    )

    val listState = rememberLazyListState()
    val density = LocalDensity.current
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
            .graphicsLayer { translationX = slideProgress * screenWidthPx }
            .background(palette.pageBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pageCanvasBackground()
                .collapsingHeaderListScroll(headerScroll, listState),
            contentPadding = PaddingValues(bottom = 48.dp),
        ) {
            item(key = "amenities_top_inset") {
                Spacer(Modifier.height(topContentInset))
            }

            if (chipCategories.isNotEmpty()) {
                item(key = "place-offer-chips") {
                    PlaceOfferChipsContent(
                        categories = chipCategories,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                }
            }

            listCategories.forEach { category ->
                item(key = "category-${category.title}") {
                    Text(
                        text = category.title,
                        color = palette.foreground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    )
                }
                itemsIndexed(
                    items = category.items,
                    key = { _, item -> "${category.title}-${item.label}" },
                ) { index, item ->
                    AmenityListRow(
                        item = item,
                        showDivider = index < category.items.lastIndex,
                    )
                }
            }
        }

        CollapsingSubpageScreenHeader(
            title = "What this place offers",
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = "Back",
            subtitle = restaurant.name,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
        )
    }
}

@Composable
private fun AmenityListRow(
    item: RestaurantAmenityItem,
    showDivider: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = item.icon.toImageVector(),
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = item.label,
                color = palette.foreground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 20.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = palette.border,
                thickness = 1.dp,
            )
        }
    }
}

internal fun AmenityIconType.toImageVector(): ImageVector = when (this) {
    AmenityIconType.AccessTime -> Icons.Outlined.AccessTime
    AmenityIconType.Wifi -> Icons.Outlined.Wifi
    AmenityIconType.Parking -> Icons.Outlined.LocalParking
    AmenityIconType.Phone -> Icons.Outlined.Phone
    AmenityIconType.Terrace -> Icons.Outlined.Terrain
    AmenityIconType.PrivateDining -> Icons.Outlined.Restaurant
    AmenityIconType.BarSeat -> Icons.Outlined.LocalBar
    AmenityIconType.Wheelchair -> Icons.Outlined.WheelchairPickup
    AmenityIconType.HighChair -> Icons.Outlined.ChildCare
    AmenityIconType.Restroom -> Icons.Outlined.Wc
    AmenityIconType.AcUnit -> Icons.Outlined.AcUnit
    AmenityIconType.Valet -> Icons.Outlined.DirectionsCar
    AmenityIconType.Reservations -> Icons.Outlined.EventSeat
    AmenityIconType.WalkIns -> Icons.Outlined.RoomService
    AmenityIconType.Sommelier -> Icons.Outlined.WineBar
    AmenityIconType.Vegan -> Icons.Outlined.Eco
    AmenityIconType.GlutenFree -> Icons.Outlined.NoFood
    AmenityIconType.KidsMenu -> Icons.Outlined.RestaurantMenu
    AmenityIconType.Halal -> Icons.Outlined.RestaurantMenu
    AmenityIconType.LiveMusic -> Icons.Outlined.MusicNote
    AmenityIconType.Quiet -> Icons.Outlined.VolumeOff
    AmenityIconType.Romantic -> Icons.Outlined.FavoriteBorder
    AmenityIconType.PetFriendly -> Icons.Outlined.Pets
    AmenityIconType.Corkage -> Icons.Outlined.WineBar
    AmenityIconType.Birthday -> Icons.Outlined.Cake
    AmenityIconType.DressCode -> Icons.Outlined.Checkroom
    AmenityIconType.CardPayment -> Icons.Outlined.CreditCard
    AmenityIconType.Cash -> Icons.Outlined.Payments
    AmenityIconType.Cuisine -> Icons.Outlined.RestaurantMenu
    AmenityIconType.DataConnection -> Icons.Outlined.SignalCellularAlt
    AmenityIconType.OutdoorHeating -> Icons.Outlined.WbSunny
    AmenityIconType.FullBar -> Icons.Outlined.LocalBar
    AmenityIconType.Takeout -> Icons.Outlined.TakeoutDining
}
