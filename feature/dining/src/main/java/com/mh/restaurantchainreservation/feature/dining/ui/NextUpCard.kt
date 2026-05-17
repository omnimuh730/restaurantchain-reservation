package com.mh.restaurantchainreservation.feature.dining.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.HubSurfaceCardDefaults
import com.mh.restaurantchainreservation.core.designsystem.components.hubSurfaceCard
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

/** HTML reference artboard (phone shell scales this proportionally). */
private const val DesignBaseWidth = 1024f
/** Slightly larger type on device vs. raw HTML px. */
private const val TextScaleBoost = 1.12f
private const val DesignLeftColumn = 585f
private const val DesignRightColumn = 332f
private const val DesignColumnGap = 36f

private val Pink = Color(0xFFEF3F67)
private val PinkSoft = Color(0xFFFDEAF0)
private val PinkLine = Color(0xFFF6A0B5)
private val TextDark = Color(0xFF242424)
private val TextGray = Color(0xFF686868)
private val DashColor = Color(0xFFD8D8D8)

/** Matches [BookingCard] restaurant title. */
private val BookingCardRestaurantTitleSize = 17.sp

/** Shared sans-serif styles for every label on this card. */
private fun cardTextStyle(
    fontSize: TextUnit,
    color: Color,
    fontWeight: FontWeight = FontWeight.SemiBold,
    letterSpacing: TextUnit = TextUnit.Unspecified,
): TextStyle = TextStyle(
    color = color,
    fontSize = fontSize,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
)

/**
 * All dimensions from the HTML mockup, scaled by [cardWidth] / 1024 (same as the JS scaleCard()).
 */
private data class ReservationScaledSpec(
    val scale: Float,
    val columnGap: Dp,
    val leftColumnWeight: Float,
    val rightColumnWeight: Float,
    val imageRadius: Dp,
    val imageBaseHeight: Dp,
    val stripWidth: Dp,
    val stripDotSize: Dp,
    val titleTopMargin: Dp,
    val titleLetterSpacing: TextUnit,
    val nameLineWidth: Dp,
    val nameLineHeight: Dp,
    val nameLineTop: Dp,
    val nameLineBottom: Dp,
    val contactDashVertical: Dp,
    val headerHeight: Dp,
    val headerIconSize: Dp,
    val headerIconGap: Dp,
    val headerTitleSize: TextUnit,
    val headerLetterSpacing: TextUnit,
    val headerLineTop: Dp,
    val headerLineBottom: Dp,
    val eventIconSize: Dp,
    val eventIconGap: Dp,
    val eventRowPadding: Dp,
    val eventTextSize: TextUnit,
    val qrTopMargin: Dp,
    val qrRadius: Dp,
    val qrBorderWidth: Dp,
    val qrPaddingH: Dp,
    val qrPaddingV: Dp,
    val qrTitleSize: TextUnit,
    val qrCodeTextSize: TextUnit,
    val qrLabelGap: Dp,
    val qrBeforeImageGap: Dp,
    val chevronSize: Dp,
)

private fun scaledSpec(cardWidth: Dp): ReservationScaledSpec {
    val s = (cardWidth.value / DesignBaseWidth).coerceIn(0.30f, 1f)
    fun sd(value: Float): Dp = (value * s).dp
    fun ss(value: Float): TextUnit = (value * s * TextScaleBoost).sp

    return ReservationScaledSpec(
        scale = s,
        columnGap = sd(DesignColumnGap),
        leftColumnWeight = DesignLeftColumn,
        rightColumnWeight = DesignRightColumn,
        imageRadius = sd(24f),
        imageBaseHeight = sd(438f),
        stripWidth = sd(42f),
        stripDotSize = sd(10f),
        titleTopMargin = sd(24f),
        titleLetterSpacing = ss(1f),
        nameLineWidth = sd(72f),
        nameLineHeight = sd(4f),
        nameLineTop = sd(16f),
        nameLineBottom = sd(28f),
        contactDashVertical = sd(24f),
        headerHeight = sd(72f),
        headerIconSize = sd(66f),
        headerIconGap = sd(22f),
        headerTitleSize = ss(28f),
        headerLetterSpacing = ss(5f),
        headerLineTop = sd(30f),
        headerLineBottom = sd(22f),
        eventIconSize = sd(62f),
        eventIconGap = sd(24f),
        eventRowPadding = sd(17f),
        eventTextSize = ss(26f),
        qrTopMargin = sd(18f),
        qrRadius = sd(22f),
        qrBorderWidth = sd(2f),
        qrPaddingH = sd(22f),
        qrPaddingV = sd(12f),
        qrTitleSize = ss(22f),
        qrCodeTextSize = ss(25f),
        qrLabelGap = sd(4f),
        qrBeforeImageGap = sd(8f),
        chevronSize = sd(20f),
    )
}

@Composable
fun NextUpCard(
    booking: Booking,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val spec = remember(maxWidth) { scaledSpec(maxWidth) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .hubSurfaceCard(palette = palette, onClick = onClick)
                .padding(HubSurfaceCardDefaults.ContentPadding),
        ) {
            ReservationCardBody(
                booking = booking,
                spec = spec,
            )
        }
    }
}

@Composable
private fun ReservationCardBody(
    booking: Booking,
    spec: ReservationScaledSpec,
) {
    val density = LocalDensity.current
    val gapPx = with(density) { spec.columnGap.roundToPx() }
    val qrGapPx = with(density) { spec.qrTopMargin.roundToPx() }
    val weightSum = spec.leftColumnWeight + spec.rightColumnWeight

    SubcomposeLayout(modifier = Modifier.fillMaxWidth()) { constraints ->
        val totalWidth = constraints.maxWidth
        val leftWidth = ((totalWidth - gapPx) * (spec.leftColumnWeight / weightSum)).toInt().coerceAtLeast(0)
        val rightWidth = (totalWidth - gapPx - leftWidth).coerceAtLeast(0)
        val leftWidthDp = with(density) { leftWidth.toDp() }
        val rightWidthDp = with(density) { rightWidth.toDp() }

        val footerPlaceable = subcompose("footer") {
            LeftColumnFooter(
                booking = booking,
                spec = spec,
                modifier = Modifier.width(leftWidthDp),
            )
        }.first().measure(Constraints.fixedWidth(leftWidth))

        val eventsPlaceable = subcompose("events") {
            EventDetailsBlock(
                booking = booking,
                spec = spec,
                modifier = Modifier.width(rightWidthDp),
            )
        }.first().measure(Constraints.fixedWidth(rightWidth))

        val targetImagePx = with(density) { spec.imageBaseHeight.roundToPx() }
        val imageHeightPx = maxOf(eventsPlaceable.height, targetImagePx)
        val minQrPanelPx = with(density) { 72.dp.roundToPx() }
        val qrPanelHeightPx = (imageHeightPx + footerPlaceable.height - eventsPlaceable.height - qrGapPx)
            .coerceAtLeast(minQrPanelPx)

        val qrPlaceable = subcompose("qr") {
            QrReservationPanel(
                confirmationNo = booking.confirmationNo,
                spec = spec,
                panelHeight = with(density) { qrPanelHeightPx.toDp() },
                modifier = Modifier.width(rightWidthDp),
            )
        }.first().measure(Constraints.fixed(rightWidth, qrPanelHeightPx))

        val imagePlaceable = subcompose("image") {
            RestaurantImageWithStrip(
                booking = booking,
                spec = spec,
                modifier = Modifier
                    .width(leftWidthDp)
                    .height(with(density) { imageHeightPx.toDp() }),
            )
        }.first().measure(Constraints.fixed(leftWidth, imageHeightPx))

        val layoutHeight = imageHeightPx + footerPlaceable.height

        layout(totalWidth, layoutHeight) {
            imagePlaceable.placeRelative(0, 0)
            footerPlaceable.placeRelative(0, imageHeightPx)
            eventsPlaceable.placeRelative(leftWidth + gapPx, 0)
            qrPlaceable.placeRelative(
                x = leftWidth + gapPx,
                y = eventsPlaceable.height + qrGapPx,
            )
        }
    }
}

@Composable
private fun RestaurantImageWithStrip(
    booking: Booking,
    spec: ReservationScaledSpec,
    modifier: Modifier = Modifier,
) {
    val imageShape = RoundedCornerShape(spec.imageRadius)
    val stripShape = RoundedCornerShape(
        topStart = spec.imageRadius,
        bottomStart = spec.imageRadius,
    )

    Box(
        modifier = modifier
            .clip(imageShape)
            .background(Color(0xFFE8E8E8)),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(booking.image)
                .crossfade(true)
                .build(),
            contentDescription = booking.restaurant,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        PinkAccentStrip(
            dotSize = spec.stripDotSize,
            modifier = Modifier
                .width(spec.stripWidth)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .clip(stripShape),
        )
    }
}

@Composable
private fun PinkAccentStrip(
    dotSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(Pink),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            repeat(7) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.28f)),
                )
            }
        }
    }
}

@Composable
private fun LeftColumnFooter(
    booking: Booking,
    spec: ReservationScaledSpec,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(spec.titleTopMargin))
        Text(
            text = booking.restaurant,
            style = cardTextStyle(
                fontSize = BookingCardRestaurantTitleSize,
                color = palette.foreground,
                fontWeight = FontWeight.ExtraBold,
            ),
            lineHeight = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(spec.nameLineTop))
        Box(
            modifier = Modifier
                .width(spec.nameLineWidth)
                .height(spec.nameLineHeight)
                .clip(RoundedCornerShape(999.dp))
                .background(Pink),
        )
        Spacer(Modifier.height(spec.nameLineBottom))
        ContactInfoGrid(
            address = booking.address,
            phone = booking.phone,
            spec = spec,
        )
    }
}

private fun splitAddressLines(address: String): List<String> {
    val parts = address.split(", ").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.size >= 3) {
        val line1 = parts.dropLast(1).joinToString(", ") + ","
        return listOf(line1, parts.last())
    }
    if (parts.size == 2) return listOf(parts[0] + ",", parts[1])
    return listOf(address)
}

/** Matches HTML `.info-grid`: fixed icon column + text column + divider in text column. */
@Composable
private fun ContactInfoGrid(
    address: String,
    phone: String,
    spec: ReservationScaledSpec,
) {
    val addressLines = splitAddressLines(address)
    val textIndent = spec.eventIconSize + spec.eventIconGap
    val textStyle = cardTextStyle(
        fontSize = spec.eventTextSize,
        color = TextGray,
        fontWeight = FontWeight.SemiBold,
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        IconTextGridRow(
            iconColumn = spec.eventIconSize,
            iconGap = spec.eventIconGap,
            verticalAlignment = Alignment.Top,
            icon = {
                EventCircleIcon(
                    imageVector = Icons.Outlined.LocationOn,
                    spec = spec,
                )
            },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    addressLines.forEach { line ->
                        Text(
                            text = line,
                            style = textStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
        )
        DottedDivider(
            color = DashColor,
            strokeWidth = 2.dp * spec.scale,
            modifier = Modifier
                .padding(
                    start = textIndent,
                    top = spec.contactDashVertical,
                    bottom = spec.contactDashVertical,
                )
                .fillMaxWidth(),
        )
        IconTextGridRow(
            iconColumn = spec.eventIconSize,
            iconGap = spec.eventIconGap,
            verticalAlignment = Alignment.CenterVertically,
            icon = {
                EventCircleIcon(
                    imageVector = Icons.Outlined.Phone,
                    spec = spec,
                )
            },
            content = {
                Text(
                    text = phone,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}

@Composable
private fun EventCircleIcon(
    imageVector: ImageVector,
    spec: ReservationScaledSpec,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(spec.eventIconSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(PinkSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(spec.eventIconSize * 0.46f),
            )
        }
    }
}

/** HTML `.event-row` / `.info-grid` shared column alignment. */
@Composable
private fun IconTextGridRow(
    iconColumn: Dp,
    iconGap: Dp,
    icon: @Composable BoxScope.() -> Unit,
    content: @Composable () -> Unit,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = verticalAlignment,
    ) {
        Box(
            modifier = Modifier.width(iconColumn),
            contentAlignment = Alignment.TopCenter,
            content = icon,
        )
        Spacer(Modifier.width(iconGap))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun EventDetailsBlock(
    booking: Booking,
    spec: ReservationScaledSpec,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        NextUpHeaderRow(spec = spec)
        NextUpHeaderDivider(spec = spec)
        EventRowsSection(
            booking = booking,
            spec = spec,
        )
    }
}

@Composable
private fun NextUpHeaderRow(spec: ReservationScaledSpec) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.headerHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(spec.headerIconSize)
                .clip(CircleShape)
                .background(Pink),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(spec.headerIconSize * 0.48f),
            )
        }
        Spacer(Modifier.width(spec.headerIconGap))
        Text(
            text = stringResource(I18nR.string.dining_next_up).uppercase(),
            style = cardTextStyle(
                fontSize = spec.headerTitleSize,
                color = Pink,
                fontWeight = FontWeight.Black,
                letterSpacing = spec.headerLetterSpacing,
            ),
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun NextUpHeaderDivider(spec: ReservationScaledSpec) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spec.headerLineTop, bottom = spec.headerLineBottom),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(spec.qrBorderWidth)
                .align(Alignment.Center),
        ) {
            drawLine(
                color = PinkLine,
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = size.height,
            )
        }
        val palette = LocalRestaurantPalette.current
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(palette.cardSurface)
                .padding(horizontal = 14.dp * spec.scale),
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(spec.chevronSize),
            )
        }
    }
}

@Composable
private fun EventRowsSection(
    booking: Booking,
    spec: ReservationScaledSpec,
) {
    val rows = listOf(
        Icons.Outlined.Celebration to occasionDisplayValue(booking),
        Icons.Outlined.CalendarMonth to booking.date,
        Icons.Outlined.AccessTime to booking.time,
    )
    val textStyle = cardTextStyle(
        fontSize = spec.eventTextSize,
        color = TextDark,
        fontWeight = FontWeight.SemiBold,
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEachIndexed { index, (icon, value) ->
            EventGridRow(
                icon = icon,
                value = value,
                spec = spec,
                textStyle = textStyle,
            )
            if (index < rows.lastIndex) {
                DottedDivider(
                    color = DashColor,
                    strokeWidth = 2.dp * spec.scale,
                )
            }
        }
    }
}

@Composable
private fun EventGridRow(
    icon: ImageVector,
    value: String,
    spec: ReservationScaledSpec,
    textStyle: TextStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spec.eventRowPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EventCircleIcon(
            imageVector = icon,
            spec = spec,
        )
        Spacer(Modifier.width(spec.eventIconGap))
        Text(
            text = value,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun occasionDisplayValue(booking: Booking): String {
    return booking.occasion?.takeIf { it.isNotBlank() }
        ?: stringResource(I18nR.string.detail_occasion_special)
}

@Composable
private fun DottedDivider(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth),
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(6.dp.toPx() * (size.height / 2.dp.toPx()).coerceAtLeast(0.5f), 6.dp.toPx()),
                0f,
            ),
        )
    }
}

@Composable
private fun QrReservationPanel(
    confirmationNo: String,
    spec: ReservationScaledSpec,
    panelHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(spec.qrRadius)

    Column(
        modifier = modifier
            .height(panelHeight)
            .fillMaxWidth()
            .clip(shape)
            .border(spec.qrBorderWidth, PinkLine, shape)
            .background(PinkSoft.copy(alpha = 0.35f))
            .padding(horizontal = spec.qrPaddingH, vertical = spec.qrPaddingV),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spec.qrLabelGap),
        ) {
            Text(
                text = stringResource(I18nR.string.dining_next_up_reservation_number),
                style = cardTextStyle(
                    fontSize = spec.qrTitleSize,
                    color = Pink,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = confirmationNo,
                style = cardTextStyle(
                    fontSize = spec.qrCodeTextSize,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp * spec.scale,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(spec.qrBeforeImageGap))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val qrSide = minOf(maxWidth, maxHeight) * 0.9f
                DeterministicQrCode(
                    code = confirmationNo,
                    modifier = Modifier
                        .size(qrSide)
                        .align(Alignment.Center),
                    color = TextDark,
                )
            }
        }
    }
}

@Composable
fun EmptyNextCard(
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = modifier
            .hubSurfaceCard(palette = palette)
            .padding(HubSurfaceCardDefaults.ContentPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(I18nR.string.dining_no_upcoming_plans).uppercase(),
                color = palette.mutedForeground,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(I18nR.string.dining_zero_upcoming),
            color = palette.foreground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(I18nR.string.dining_discover_prompt),
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun StatsGrid(
    placesVisited: Int,
    totalBookings: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatTile(
            value = placesVisited,
            label = stringResource(I18nR.string.dining_places_visited),
            modifier = Modifier.weight(1f),
        )
        StatTile(
            value = totalBookings,
            label = stringResource(I18nR.string.dining_total_bookings),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatTile(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current

    Column(
        modifier = modifier
            .hubSurfaceCard(palette = palette)
            .padding(HubSurfaceCardDefaults.ContentPadding),
    ) {
        Text(
            text = value.toString(),
            color = palette.foreground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = palette.mutedForeground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
