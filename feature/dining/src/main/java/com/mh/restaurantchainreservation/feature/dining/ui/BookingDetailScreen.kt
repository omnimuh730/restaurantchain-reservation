package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TableRestaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.DetailCollapsingMetrics
import com.mh.restaurantchainreservation.core.designsystem.components.DetailFloatingHeartButton
import com.mh.restaurantchainreservation.core.designsystem.components.DetailFloatingIconButton
import com.mh.restaurantchainreservation.core.designsystem.components.DetailFloatingToolbar
import com.mh.restaurantchainreservation.core.designsystem.components.DetailHeroScrollOverlay
import com.mh.restaurantchainreservation.core.designsystem.components.detailHeroParallax
import com.mh.restaurantchainreservation.core.designsystem.components.detailMorphingSheetBackground
import com.mh.restaurantchainreservation.core.designsystem.components.detailMorphingSheetShape
import com.mh.restaurantchainreservation.core.designsystem.components.rememberDetailCollapseProgress
import com.mh.restaurantchainreservation.core.designsystem.components.rememberDetailTransitionThresholds
import com.mh.restaurantchainreservation.core.designsystem.components.LocalNavContentBottomPadding
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantLocationMap
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.components.trackBottomNavScroll
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.DiscoverData
import com.mh.restaurantchainreservation.core.model.Restaurant
import com.mh.restaurantchainreservation.core.model.WishlistStore
import com.mh.restaurantchainreservation.core.model.mapCoordinate
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.data.isGuestInviteBooking
import com.mh.restaurantchainreservation.feature.dining.data.displayCuisineLabels
import com.mh.restaurantchainreservation.feature.dining.data.displaySeatingLabels

private val SheetTopRadius = 34.dp
private val HeaderSheetShape = RoundedCornerShape(topStart = SheetTopRadius, topEnd = SheetTopRadius)
private val HeroHeight = 288.dp
private val DetailInfoHorizontalPadding = 24.dp
private val DetailSheetTopPadding = 28.dp
private val DetailSectionVerticalPadding = 24.dp
private val DetailSectionTitleSpacing = 20.dp
private val HeroOverlayBottomPadding = 20.dp
/** Bottom edge of the large hero title (above the host row) in scroll content coordinates. */
private val BookingHeroTitleBottomInContent: Dp
    get() = HeroHeight - (SheetTopRadius + HeroOverlayBottomPadding) - (10.dp + 22.dp)
private val SectionSpacing = HubSurfaceCardDefaults.SectionSpacing
private val ReservationIconSize = 40.dp
private val ReservationIconGap = 14.dp
private val ReservationPink = RestaurantColors.Dining.pink
private val ReservationPinkSoft = RestaurantColors.Dining.pinkSoft

private val GalleryExtras = listOf(
    "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=1200&h=800&fit=crop",
    "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200&h=800&fit=crop",
    "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&h=800&fit=crop",
)

private fun galleryImagesFor(booking: Booking): List<String> {
    val restaurant = DiscoverData.findById(booking.id)
    val primary = restaurant?.image ?: booking.image
    return listOf(primary) + GalleryExtras
}

private fun Modifier.detailSheetTopRoundedBackground(color: Color): Modifier = drawBehind {
    val topRadius = SheetTopRadius.toPx()
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, size.width, size.height),
                topLeft = CornerRadius(topRadius, topRadius),
                topRight = CornerRadius(topRadius, topRadius),
                bottomRight = CornerRadius.Zero,
                bottomLeft = CornerRadius.Zero,
            ),
        )
    }
    drawPath(path, color = color)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookingDetailScreen(
    booking: Booking,
    onBack: () -> Unit,
    onManage: () -> Unit,
    onCancel: () -> Unit = onManage,
    onScanQR: () -> Unit,
    onShowQR: () -> Unit,
    onInvite: () -> Unit,
    onBookAgain: () -> Unit,
    onViewReceipt: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val scroll = rememberScrollState()

    val isScheduled = booking.status == BookingStatus.Confirmed
    val isPending = booking.status == BookingStatus.Pending
    val isRejected = booking.status == BookingStatus.Rejected
    val isCancelled = booking.status == BookingStatus.Cancelled || booking.status == BookingStatus.NoShow
    val isCompleted = booking.status == BookingStatus.Completed

    val restaurant = remember(booking.id) { DiscoverData.findById(booking.id) }
    val wishlistRestaurant = remember(booking.id, restaurant) { restaurantForBooking(booking, restaurant) }
    val savedIds by WishlistStore.savedRestaurantIds.collectAsState()
    val saved = wishlistRestaurant.id in savedIds
    val galleryImages = remember(booking.id, booking.image) { galleryImagesFor(booking) }
    val collapseRangePx = remember(density) { DetailCollapsingMetrics.heroScrollRangePx(density) }
    val collapseProgress = rememberDetailCollapseProgress(scroll, collapseRangePx)
    val transitionThresholds = rememberDetailTransitionThresholds(
        titleBottomFromContentTop = BookingHeroTitleBottomInContent,
    )
    val sheetShape = detailMorphingSheetShape(collapseProgress)
    val heroScrollOffsetPx = scroll.value
    val navBottomPadding = LocalNavContentBottomPadding.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .trackBottomNavScroll(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HeroHeight)
                        .detailHeroParallax(heroScrollOffsetPx),
                ) {
                    BookingHeroCarousel(
                        booking = booking,
                        galleryImages = galleryImages,
                        restaurant = restaurant,
                    )
                    DetailHeroScrollOverlay(
                        collapseProgress = collapseProgress,
                        transitionThresholds = transitionThresholds,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -SheetTopRadius)
                        .detailMorphingSheetBackground(palette.pageBackground, collapseProgress)
                        .clip(sheetShape)
                        .padding(horizontal = DetailInfoHorizontalPadding),
                ) {
                    Spacer(Modifier.height(DetailSheetTopPadding))

                    ReservationSection(
                        booking = booking,
                        showQuickActions = isScheduled,
                        onShowQR = onShowQR,
                        onInvite = onInvite,
                        onScanQR = onScanQR,
                    )

                if (isPending) {
                    Spacer(Modifier.height(SectionSpacing))
                    StatusInfoBlock(
                        title = stringResource(I18nR.string.detail_pending_title),
                        description = stringResource(I18nR.string.detail_pending_desc),
                        accent = palette.warning,
                        container = palette.warning.copy(alpha = 0.10f),
                    ) {
                        Text(
                            text = stringResource(I18nR.string.detail_request_code).uppercase(),
                            color = palette.mutedForeground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = booking.confirmationNo,
                            color = palette.foreground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }

                if (isRejected) {
                    Spacer(Modifier.height(SectionSpacing))
                    StatusInfoBlock(
                        title = stringResource(I18nR.string.detail_rejected_title),
                        description = stringResource(I18nR.string.detail_rejected_desc),
                        accent = palette.destructive,
                        container = palette.destructive.copy(alpha = 0.10f),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            DetailActionButton(
                                text = stringResource(I18nR.string.booking_action_request_again),
                                icon = Icons.Outlined.Refresh,
                                primary = true,
                                onClick = onBookAgain,
                                modifier = Modifier.weight(1f),
                            )
                            DetailActionButton(
                                text = stringResource(I18nR.string.booking_action_delete),
                                icon = null,
                                primary = false,
                                destructive = true,
                                onClick = onDeleteRequest,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                BookingDetailInsetDivider()
                RestaurantInfoSection(booking = booking, restaurant = restaurant)

                if (isScheduled) {
                    Spacer(Modifier.height(SectionSpacing))
                    ReservationManagementActions(
                        onModify = onManage,
                        onCancel = onCancel,
                        showModify = !booking.isGuestInviteBooking(),
                    )
                }

                if (isCancelled) {
                    Spacer(Modifier.height(SectionSpacing))
                    DetailActionButton(
                        text = stringResource(I18nR.string.detail_book_again),
                        icon = Icons.Outlined.Refresh,
                        primary = true,
                        onClick = onBookAgain,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (isCompleted) {
                    Spacer(Modifier.height(SectionSpacing))
                    if (booking.receipt != null && !booking.isGuestInviteBooking()) {
                        DetailActionButton(
                            text = stringResource(I18nR.string.detail_view_receipt),
                            icon = Icons.Outlined.Receipt,
                            primary = false,
                            onClick = onViewReceipt,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    DetailActionButton(
                        text = stringResource(I18nR.string.detail_book_again),
                        icon = Icons.Outlined.Refresh,
                        primary = true,
                        onClick = onBookAgain,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(40.dp + navBottomPadding))
                }
            }
        }

        DetailFloatingToolbar(
            title = booking.restaurant,
            collapseProgress = collapseProgress,
            onBack = onBack,
            backContentDescription = stringResource(I18nR.string.detail_header_back),
            transitionThresholds = transitionThresholds,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f),
        ) { plateAlpha ->
            DetailFloatingIconButton(
                onClick = onInvite,
                plateAlpha = plateAlpha,
                contentDescription = stringResource(I18nR.string.detail_header_share),
            ) {
                Icon(
                    Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(17.dp),
                )
            }
            DetailFloatingHeartButton(
                active = saved,
                plateAlpha = plateAlpha,
                onClick = { WishlistStore.onHeartTap(wishlistRestaurant) },
                contentDescription = stringResource(
                    if (saved) I18nR.string.detail_unsave else I18nR.string.detail_save,
                ),
            )
        }
    }
}

private fun restaurantForBooking(booking: Booking, restaurant: Restaurant?): Restaurant {
    if (restaurant != null) return restaurant
    return Restaurant(
        id = booking.id,
        name = booking.restaurant,
        cuisine = booking.cuisine,
        rating = booking.rating ?: 4.5,
        reviews = 0,
        price = "$$",
        distance = "",
        image = booking.image,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookingHeroCarousel(
    booking: Booking,
    galleryImages: List<String>,
    restaurant: Restaurant?,
) {
    val palette = LocalRestaurantPalette.current
    val pagerState = rememberPagerState(pageCount = { galleryImages.size })
    val hostName = restaurantHostName(booking, restaurant)
    val heroOverlayBottom = SheetTopRadius + HeroOverlayBottomPadding

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
            .background(palette.mutedSurface),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            AsyncImage(
                model = galleryImages[page],
                contentDescription = booking.restaurant,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(196.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.68f),
                        ),
                    ),
                ),
        )

        if (galleryImages.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RestaurantColors.Base.black.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${galleryImages.size}",
                    color = RestaurantColors.Base.white,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = DetailInfoHorizontalPadding,
                    end = DetailInfoHorizontalPadding,
                    bottom = heroOverlayBottom,
                ),
        ) {
            Text(
                text = booking.restaurant,
                color = RestaurantColors.Base.white,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = ReservationPink,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(I18nR.string.detail_hosted_by, hostName),
                    color = RestaurantColors.Base.white.copy(alpha = 0.92f),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun restaurantHostName(booking: Booking, restaurant: Restaurant?): String {
    if (booking.restaurant.contains("Sakura", ignoreCase = true)) return "Chef Tanaka"
    if (booking.restaurant.contains("Le Petit", ignoreCase = true)) return "Chef Laurent"
    if (booking.restaurant.contains("Bella Napoli", ignoreCase = true)) return "Marco Rossi"
    return restaurant?.area?.takeIf { it.isNotBlank() }
        ?: booking.restaurant.split(" ").firstOrNull()
        ?: booking.restaurant
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReservationSection(
    booking: Booking,
    showQuickActions: Boolean,
    onShowQR: () -> Unit,
    onInvite: () -> Unit,
    onScanQR: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DetailSectionVerticalPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(I18nR.string.detail_your_reservation),
                color = palette.foreground,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(booking = booking, checkedInIds = null)
        }
        if (showQuickActions) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ShareActionTile(
                    label = stringResource(I18nR.string.detail_show_qr),
                    icon = Icons.Outlined.QrCode,
                    onClick = onShowQR,
                    modifier = Modifier.weight(1f),
                )
                ShareActionTile(
                    label = stringResource(I18nR.string.detail_invite_guests),
                    icon = Icons.Outlined.PersonAdd,
                    onClick = onInvite,
                    modifier = Modifier.weight(1f),
                )
                ShareActionTile(
                    label = stringResource(I18nR.string.scan_step_scan),
                    icon = Icons.Outlined.QrCodeScanner,
                    onClick = onScanQR,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(DetailSectionVerticalPadding))
            BookingDetailInsetDivider()
            Spacer(Modifier.height(DetailSectionVerticalPadding))
        } else {
            Spacer(Modifier.height(28.dp))
        }
        ReservationDetailsContent(booking = booking)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReservationDetailsContent(booking: Booking) {
    val reservationDate = remember(booking.date) {
        booking.date.split(",").joinToString(",") { part -> part.trim() }
    }
    val contactName = booking.contactName?.takeIf { it.isNotBlank() }
        ?: stringResource(I18nR.string.detail_reservation_guest)
    val occasionValue = booking.occasion?.let { resolveOccasionLabel(it) }
        ?: stringResource(I18nR.string.detail_reservation_occasion_none)
    val seatingLabels = remember(booking.id, booking.seating, booking.seatingLabels) {
        booking.displaySeatingLabels()
    }
    val cuisineLabels = remember(booking.id, booking.cuisine, booking.cuisineLabels) {
        booking.displayCuisineLabels()
    }
    val preferenceLabels = remember(
        booking.id,
        seatingLabels,
        cuisineLabels,
        booking.vibeLabels,
        booking.amenityLabels,
    ) {
        buildList {
            addAll(seatingLabels)
            addAll(cuisineLabels)
            addAll(booking.vibeLabels)
            addAll(booking.amenityLabels)
        }.distinct()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = DetailSectionVerticalPadding)) {
            BookingDetailSectionTitle(
                text = stringResource(I18nR.string.detail_booking_details),
            )
            Spacer(Modifier.height(DetailSectionTitleSpacing))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ReservationInfoRow(
                    icon = Icons.Outlined.CalendarMonth,
                    value = reservationDate,
                    modifier = Modifier.weight(1f),
                )
                ReservationInfoRow(
                    icon = Icons.Outlined.AccessTime,
                    value = booking.time,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ReservationInfoRow(
                    icon = Icons.Outlined.Group,
                    value = stringResource(I18nR.string.detail_table_for, booking.guests),
                    modifier = Modifier.weight(1f),
                )
                ReservationInfoRow(
                    icon = Icons.Outlined.Celebration,
                    value = occasionValue,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            ReservationContactRow(
                name = contactName,
                phone = booking.phone,
            )
        }

        val showPreferencesSection = preferenceLabels.isNotEmpty() || booking.specialRequest != null
        if (showPreferencesSection) {
            BookingDetailInsetDivider()
            Column(
                modifier = Modifier.padding(
                    top = DetailSectionVerticalPadding,
                    bottom = DetailSectionVerticalPadding,
                ),
            ) {
                BookingDetailSectionTitle(
                    text = stringResource(I18nR.string.detail_reservation_preferences),
                )
                Spacer(Modifier.height(DetailSectionTitleSpacing))
                if (preferenceLabels.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        preferenceLabels.forEach { label ->
                            ReservationPreferenceChip(label = label)
                        }
                    }
                }
                booking.specialRequest?.let { request ->
                    if (preferenceLabels.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                    }
                    ReservationSpecialRequestBubble(text = request)
                }
            }
        }
    }
}

@Composable
private fun ReservationInfoRow(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReservationEventCircleIcon(imageVector = icon)
        Spacer(Modifier.width(ReservationIconGap))
        Text(
            text = value,
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReservationContactRow(
    name: String,
    phone: String,
) {
    val palette = LocalRestaurantPalette.current
    val initials = remember(name) { contactInitials(name) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(ReservationIconSize)
                .clip(CircleShape)
                .background(ReservationPinkSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = ReservationPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(ReservationIconGap))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = phone,
                    color = palette.mutedForeground,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun contactInitials(name: String): String =
    name.trim()
        .split(Regex("\\s+"))
        .mapNotNull { part -> part.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "?" }

@Composable
private fun ReservationSpecialRequestBubble(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.75f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    )
}

@Composable
private fun ReservationPreferenceChip(label: String) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Text(
        text = label,
        color = palette.foreground,
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(shape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border, shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun ReservationManagementActions(
    onModify: () -> Unit,
    onCancel: () -> Unit,
    showModify: Boolean = true,
) {
    val cancelTint = RestaurantColors.Text.primary
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showModify) {
            BookingOutlinedAction(
                label = stringResource(I18nR.string.detail_modify_booking),
                icon = Icons.Outlined.Edit,
                tint = ReservationPink,
                onClick = onModify,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        BookingOutlinedAction(
            label = stringResource(I18nR.string.detail_cancel_booking),
            icon = Icons.Outlined.Cancel,
            tint = cancelTint,
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            muted = true,
        )
    }
}

@Composable
private fun BookingOutlinedAction(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    muted: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (muted) {
        palette.border.copy(alpha = 0.45f)
    } else {
        palette.border.copy(alpha = 0.65f)
    }
    val background = if (muted) palette.mutedSurface.copy(alpha = 0.35f) else palette.cardSurface
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 16.sp,
            fontWeight = if (muted) FontWeight.Medium else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ShareActionTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .height(88.dp)
            .clip(shape)
            .background(palette.mutedSurface.copy(alpha = 0.55f))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BookingDetailSectionTitle(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun BookingDetailInsetDivider(modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    HorizontalDivider(
        color = palette.border,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun RestaurantInfoSection(
    booking: Booking,
    restaurant: Restaurant?,
) {
    val resolved = remember(booking.id, restaurant) { restaurantForBooking(booking, restaurant) }
    val (lat, lng) = remember(resolved.id) { resolved.mapCoordinate() }
    val locationLine = remember(resolved.area, booking.address) {
        listOfNotNull(
            resolved.area?.takeIf { it.isNotBlank() },
            booking.address.takeIf { it.isNotBlank() },
        ).joinToString(", ").ifBlank { booking.address }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DetailSectionVerticalPadding),
    ) {
        BookingDetailSectionTitle(
            text = stringResource(I18nR.string.detail_section_restaurant_info),
        )
        RestaurantLocationMap(
            latitude = lat,
            longitude = lng,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 16.dp),
        )
        BookingDetailInfoRow(
            icon = Icons.Outlined.Place,
            title = stringResource(I18nR.string.detail_section_location),
            subtitle = locationLine,
            modifier = Modifier.padding(top = 20.dp),
        )
        BookingDetailInfoRow(
            icon = Icons.Outlined.Phone,
            title = stringResource(I18nR.string.detail_reservation_contact),
            subtitle = booking.phone,
            modifier = Modifier.padding(top = 20.dp),
        )
    }
}

@Composable
private fun BookingDetailInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.foreground,
            modifier = Modifier.size(22.dp),
        )
        Column {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = palette.mutedForeground,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ReservationEventCircleIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = ReservationIconSize,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(ReservationPinkSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = ReservationPink,
                modifier = Modifier.size(size * 0.46f),
            )
        }
    }
}

@Composable
private fun resolveOccasionLabel(occasion: String): String = when (occasion.lowercase()) {
    "birthday" -> stringResource(I18nR.string.detail_occasion_birthday)
    "anniversary" -> stringResource(I18nR.string.detail_occasion_anniversary)
    "date" -> stringResource(I18nR.string.detail_occasion_date)
    "special" -> stringResource(I18nR.string.detail_occasion_special)
    "celebration" -> stringResource(I18nR.string.detail_occasion_celebration)
    else -> occasion
}

@Composable
private fun StatusInfoBlock(
    title: String,
    description: String,
    accent: Color,
    container: Color,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .hubSurfaceCard(palette = palette)
            .padding(HubSurfaceCardDefaults.ContentPadding),
    ) {
        Text(text = title, color = accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = description, color = palette.mutedForeground, fontSize = 14.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(palette.cardSurface)
                .padding(12.dp),
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    icon: ImageVector?,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
    val container = when {
        destructive && !primary -> palette.cardSurface
        primary -> palette.brand
        else -> palette.cardSurface
    }
    val content = when {
        destructive && !primary -> palette.destructive
        primary -> RestaurantColors.Base.white
        else -> palette.foreground
    }
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .let { if (!primary) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = content, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(6.dp))
        }
        Text(text = text, color = content, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}
