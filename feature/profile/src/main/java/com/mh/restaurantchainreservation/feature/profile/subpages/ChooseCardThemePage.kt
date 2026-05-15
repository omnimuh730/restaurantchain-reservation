package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.HubThemedCardBackground
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeBackgroundBrush

private val ThemePickerOrder: List<HubCardThemeId> = listOf(
    HubCardThemeId.Ink,
    HubCardThemeId.Rose,
    HubCardThemeId.Amethyst,
    HubCardThemeId.Ocean,
    HubCardThemeId.Sunset,
    HubCardThemeId.Forest,
)

private val PatternPickerOrder: List<HubCardPattern> = listOf(
    HubCardPattern.Stars,
    HubCardPattern.Grid,
    HubCardPattern.Wave,
    HubCardPattern.Blob,
    HubCardPattern.Rays,
    HubCardPattern.None,
)

private fun HubCardPattern.displayLabel(): String = when (this) {
    HubCardPattern.Stars -> "Stars"
    HubCardPattern.Grid -> "Grid"
    HubCardPattern.Wave -> "Wave"
    HubCardPattern.Blob -> "Blob"
    HubCardPattern.Rays -> "Rays"
    HubCardPattern.None -> "Solid"
}

private const val ChooseCardSheetSteps = 2

/** Width of each theme/pattern card in the horizontal picker strips. */
private val PickerStripItemWidth = 100.dp

/** Swatch area (~3:2 letterbox vs width); shorter than before per design. */
private val PickerStripSwatchHeight = 52.dp

/** Fixed label row so every slot has the same height, selected or not. */
private val PickerStripLabelRowHeight = 28.dp

private val PickerStripFrameHeight = PickerStripSwatchHeight + PickerStripLabelRowHeight

/** Black ring thickness around the selected chip. */
private val PickerSelectedRingStroke = 2.dp

/**
 * Clear space between the inner edge of the ring and the chip content (same size as [PickerSelectedRingStroke]).
 */
private val PickerSelectedRingGap = PickerSelectedRingStroke

/** Padding from slot edge to content for every item so selected/unselected sizes match. */
private val PickerStripContentPadding = PickerSelectedRingStroke + PickerSelectedRingGap

private val PickerSwatchTopCorner = 14.dp

/** Swatch: rounded top, square bottom (meets label flush). */
private val PickerSwatchShape = RoundedCornerShape(
    topStart = PickerSwatchTopCorner,
    topEnd = PickerSwatchTopCorner,
    bottomEnd = 0.dp,
    bottomStart = 0.dp,
)

private val PickerSelectedInnerShape = RoundedCornerShape(11.dp)

/** Horizontal inset for title, card preview, section headers, footer, and strip row ends. */
private val ChooseCardSheetContentPadding = 20.dp

/**
 * Rising bottom sheet (~78% screen height) for picking theme and pattern when adding a card.
 * Two step tabs: design (color + pattern linear strips) → review.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChooseCardThemeBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    previewNickname: String,
    holder: String,
    lastFour: String,
    fullCardNumber: String,
    selectedThemeId: HubCardThemeId,
    selectedPattern: HubCardPattern,
    onThemeSelected: (HubCardThemeId) -> Unit,
    onPatternSelected: (HubCardPattern) -> Unit,
    onConfirm: () -> Unit,
) {
    if (!visible) return

    val palette = LocalRestaurantPalette.current
    val configuration = LocalConfiguration.current
    val sheetMaxHeight = (configuration.screenHeightDp * 0.78f).dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(visible) {
        if (visible) step = 0
    }

    val previewModel = SharedHubCardFaceModel(
        productLabel = previewNickname,
        holder = holder.uppercase(),
        lastFour = lastFour,
        krwBalance = 0L,
        usdBalance = 0.0,
        themeId = selectedThemeId,
        pattern = selectedPattern,
        showBalance = false,
        showDualBalance = false,
        frozen = false,
        showFullPan = false,
        fullCardNumber = fullCardNumber,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.cardSurface,
        contentColor = palette.foreground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetMaxHeight),
        ) {
            ChooseCardThemeStepTabs(
                currentStep = step,
                onStepSelect = { step = it.coerceIn(0, ChooseCardSheetSteps - 1) },
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 6.dp, bottom = 6.dp),
            ) {
                val inset = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ChooseCardSheetContentPadding)
                Column(modifier = inset) {
                    Text(
                        text = "Choose your card",
                        color = palette.foreground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Pick a theme — every card stays multi-currency.",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                    SharedHubCardFace(
                        model = previewModel,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
                when (step) {
                    0 -> {
                        Text(
                            text = "Color",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = inset,
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
                        ) {
                            items(ThemePickerOrder, key = { it.name }) { themeId ->
                                ThemePickerStripItem(
                                    themeId = themeId,
                                    selected = themeId == selectedThemeId,
                                    onClick = { onThemeSelected(themeId) },
                                )
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = "Pattern",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = inset,
                        )
                        Spacer(Modifier.height(10.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = ChooseCardSheetContentPadding),
                        ) {
                            items(PatternPickerOrder, key = { it.name }) { pattern ->
                                PatternPickerStripItem(
                                    pattern = pattern,
                                    label = pattern.displayLabel(),
                                    selected = pattern == selectedPattern,
                                    themeId = selectedThemeId,
                                    onClick = { onPatternSelected(pattern) },
                                )
                            }
                        }
                    }
                    else -> {
                        Column(modifier = inset) {
                            Text(
                                text = "Review",
                                color = palette.foreground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "You’re adding ${previewNickname} with the ${selectedThemeId.name} look and ${selectedPattern.displayLabel()} pattern.",
                                color = palette.mutedForeground,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ChooseCardSheetContentPadding)
                    .padding(bottom = 16.dp, top = 2.dp),
            ) {
                val isLast = step >= ChooseCardSheetSteps - 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.brand)
                        .clickable(
                            role = Role.Button,
                            onClick = {
                                if (isLast) {
                                    onConfirm()
                                } else {
                                    step++
                                }
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isLast) "Add card" else "Continue",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun PickerStripItemFrame(
    selected: Boolean,
    onClickLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    swatch: @Composable () -> Unit,
    label: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .width(PickerStripItemWidth)
            .height(PickerStripFrameHeight)
            .drawWithContent {
                drawContent()
                if (selected) {
                    val strokePx = PickerSelectedRingStroke.toPx()
                    val halfStroke = strokePx / 2f
                    val rw = size.width - strokePx
                    val rh = size.height - strokePx
                    if (rw > 0f && rh > 0f) {
                        val rPx = minOf(14.dp.toPx(), rw / 2f, rh / 2f)
                        drawRoundRect(
                            color = Color.Black,
                            topLeft = Offset(halfStroke, halfStroke),
                            size = Size(rw, rh),
                            cornerRadius = CornerRadius(rPx, rPx),
                            style = Stroke(width = strokePx),
                        )
                    }
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = onClickLabel,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PickerStripContentPadding)
                .then(
                    if (selected) {
                        Modifier.background(Color.White, PickerSelectedInnerShape)
                    } else {
                        Modifier
                    },
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            swatch()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripLabelRowHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun PickerStripCheckmark() {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(11.dp),
        )
    }
}

@Composable
private fun ThemePickerStripItem(
    themeId: HubCardThemeId,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val label = themeId.name
    PickerStripItemFrame(
        selected = selected,
        onClickLabel = label,
        onClick = onClick,
        modifier = modifier,
        label = label,
        swatch = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripSwatchHeight)
                    .clip(PickerSwatchShape)
                    .drawBehind {
                        drawRect(
                            brush = hubCardThemeBackgroundBrush(
                                themeId = themeId,
                                widthPx = size.width,
                                heightPx = size.height,
                                brandColor = palette.brand,
                            ),
                        )
                    },
            ) {
                if (selected) {
                    Box(Modifier.align(Alignment.TopEnd)) {
                        PickerStripCheckmark()
                    }
                }
            }
        },
    )
}

@Composable
private fun PatternPickerStripItem(
    pattern: HubCardPattern,
    label: String,
    selected: Boolean,
    themeId: HubCardThemeId,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    PickerStripItemFrame(
        selected = selected,
        onClickLabel = label,
        onClick = onClick,
        modifier = modifier,
        label = label,
        swatch = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PickerStripSwatchHeight)
                    .clip(PickerSwatchShape),
            ) {
                HubThemedCardBackground(
                    themeId = themeId,
                    patternOverride = pattern,
                    brandColor = palette.brand,
                    modifier = Modifier.matchParentSize(),
                )
                if (selected) {
                    Box(Modifier.align(Alignment.TopEnd)) {
                        PickerStripCheckmark()
                    }
                }
            }
        },
    )
}

@Composable
private fun ChooseCardThemeStepTabs(
    currentStep: Int,
    onStepSelect: (Int) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ChooseCardSheetContentPadding)
            .padding(top = 2.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(ChooseCardSheetSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        when {
                            index == currentStep -> palette.brand
                            index < currentStep -> palette.brand.copy(alpha = 0.42f)
                            else -> palette.mutedSurface
                        },
                    )
                    .clickable(
                        role = Role.Tab,
                        onClickLabel = "Step ${index + 1}",
                        onClick = { onStepSelect(index) },
                    ),
            )
        }
    }
}
