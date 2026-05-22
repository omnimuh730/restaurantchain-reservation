package com.mh.restaurantchainreservation.feature.profile.hub

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantFontFamily
import java.text.NumberFormat
import java.util.Locale

/** Web-style gold text stack (offset layers; no drop shadow). */
@Composable
private fun HubWebGoldText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
) {
    Box(modifier) {
        Text(
            text = text,
            modifier = Modifier.offset(x = 0.dp, y = 2.dp),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = style.copy(
                brush = SolidColor(RestaurantColors.Base.black.copy(alpha = 0.25f)),
            ),
        )
        Text(
            text = text,
            modifier = Modifier.offset(x = 0.dp, y = 1.dp),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = style.copy(
                brush = SolidColor(RestaurantColors.Overlay.scrimHeavy.copy(alpha = 0.9f)),
            ),
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal data class SharedHubCardFaceModel(
    val productLabel: String,
    val holder: String,
    val lastFour: String,
    val krwBalance: Long,
    val usdBalance: Double,
    val themeId: HubCardThemeId,
    val pattern: HubCardPattern,
    val showBalance: Boolean,
    val showDualBalance: Boolean = false,
    val frozen: Boolean = false,
    val showFullPan: Boolean = false,
    val fullCardNumber: String = "",
)

internal fun hubPrimaryBalanceText(krw: Long, usd: Double): String = when {
    usd > 0 && krw <= 0L -> formatUsdHub(usd)
    krw > 0 && usd <= 0.0 -> formatKrwHub(krw)
    krw.toDouble() >= usd * 1400.0 -> formatKrwHub(krw)
    usd > 0 -> formatUsdHub(usd)
    else -> formatKrwHub(krw)
}

internal fun formatKrwHub(value: Long): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    return "₩${nf.format(value)}"
}

internal fun formatUsdHub(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale.US)
    nf.maximumFractionDigits = 2
    nf.minimumFractionDigits = 2
    return nf.format(value)
}

@Composable
internal fun HubLuxuryEmvChip(modifier: Modifier = Modifier) {
    val chipShape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .size(width = 40.dp, height = 28.dp)
            .clip(chipShape)
            .background(hubChipAnodizedBrush())
            .border(1.dp, RestaurantColors.Base.white.copy(alpha = 0.26f), chipShape),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val grid = RestaurantColors.Base.black.copy(alpha = 0.24f)
            val stepX = size.width / 4f
            var x = stepX
            repeat(3) {
                drawLine(grid, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f * density)
                x += stepX
            }
            val stepY = size.height / 3f
            var y = stepY
            repeat(2) {
                drawLine(grid, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f * density)
                y += stepY
            }
            drawLine(
                color = RestaurantColors.Overlay.veilLight,
                start = Offset(0f, 1.2f * density),
                end = Offset(size.width * 0.85f, 2.8f * density),
                strokeWidth = 1.1f * density,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(3.dp)
                .clip(RoundedCornerShape(3.dp))
                .border(1.dp, RestaurantColors.Shadow.hubAmbient, RoundedCornerShape(3.dp)),
        )
    }
}

@Composable
internal fun HubMaskedExpiryGold(
    expiryWebStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    HubWebGoldText(
        text = "••/••",
        style = expiryWebStyle,
        modifier = modifier,
    )
}

@Composable
internal fun HubMaskedPanRow(
    lastFour: String,
    panWebStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    HubWebGoldText(
        text = "•••• •••• •••• $lastFour",
        style = panWebStyle,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
internal fun SharedHubCardFace(
    model: SharedHubCardFaceModel,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(32.dp)
    val palette = LocalRestaurantPalette.current
    val balancePrimaryText = remember(model.krwBalance, model.usdBalance) {
        hubPrimaryBalanceText(model.krwBalance, model.usdBalance)
    }
    val labelWeb = RestaurantColors.Base.white.copy(alpha = 0.7f)
    val goldBalanceWebStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.01).em,
            brush = hubWebCardGoldBrush(),
            fontFeatureSettings = "tnum",
        )
    }
    val panWebStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.18.em,
            brush = hubWebCardGoldBrush(),
            fontFeatureSettings = "tnum",
        )
    }
    val expiryWebStyle = remember {
        TextStyle(
            fontFamily = RestaurantFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
            brush = hubWebCardGoldBrush(),
            fontFeatureSettings = "tnum",
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.58f),
    ) {
        val wPx = with(LocalDensity.current) { maxWidth.toPx() }
        val hPx = with(LocalDensity.current) { maxHeight.toPx() }
        val baseBrush = remember(model.themeId, palette.brand, wPx, hPx) {
            hubCardThemeBackgroundBrush(
                themeId = model.themeId,
                widthPx = wPx,
                heightPx = hPx,
                brandColor = palette.brand,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(baseBrush)
                .drawBehind {
                    drawHubThemedCardDecorations(
                        themeId = model.themeId,
                        patternOverride = model.pattern,
                        brandColor = palette.brand,
                    )
                },
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    TonightLogoMark(
                        modifier = Modifier.size(18.dp),
                        color = RestaurantColors.Base.white,
                        contentDescription = null,
                    )
                    Text(
                        text = model.productLabel,
                        color = RestaurantColors.Base.white,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.02).em,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (model.frozen) {
                    Text(
                        text = "FROZEN",
                        color = RestaurantColors.Base.white,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(RestaurantColors.Base.white.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            if (model.showBalance) {
                Text(
                    text = "AVAILABLE BALANCE",
                    color = labelWeb,
                    fontFamily = RestaurantFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.14.em,
                )
                Spacer(Modifier.height(4.dp))
                if (model.showDualBalance && model.krwBalance > 0L && model.usdBalance > 0.0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HubWebGoldText(
                            text = formatKrwHub(model.krwBalance),
                            style = goldBalanceWebStyle,
                        )
                        HubWebGoldText(
                            text = "·",
                            style = goldBalanceWebStyle,
                        )
                        HubWebGoldText(
                            text = formatUsdHub(model.usdBalance),
                            style = goldBalanceWebStyle,
                        )
                    }
                } else {
                    HubWebGoldText(
                        text = balancePrimaryText,
                        style = goldBalanceWebStyle,
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HubLuxuryEmvChip()
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = RestaurantColors.Base.white,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    HubContactlessIcon(tint = RestaurantColors.Base.white)
                }
                Spacer(Modifier.height(18.dp))
            }
            Spacer(Modifier.weight(1f))
            if (model.showFullPan && model.fullCardNumber.isNotEmpty()) {
                HubWebGoldText(
                    text = model.fullCardNumber.chunked(4).joinToString(" "),
                    style = panWebStyle,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                HubMaskedPanRow(lastFour = model.lastFour, panWebStyle = panWebStyle)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "CARD HOLDER",
                        color = labelWeb,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.14.em,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = model.holder,
                        color = RestaurantColors.Base.white,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.02.em,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = labelWeb,
                        fontFamily = RestaurantFontFamily,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.14.em,
                    )
                    Spacer(Modifier.height(2.dp))
                    HubMaskedExpiryGold(
                        expiryWebStyle = expiryWebStyle,
                    )
                }
            }
        }
        }
    }
}
