package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSwatchBrush

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

@Composable
internal fun ChooseCardThemePage(
    previewNickname: String,
    holder: String,
    lastFour: String,
    fullCardNumber: String,
    selectedThemeId: HubCardThemeId,
    selectedPattern: HubCardPattern,
    onThemeSelected: (HubCardThemeId) -> Unit,
    onPatternSelected: (HubCardPattern) -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
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

    SubpageScaffold(
        title = "Choose your card",
        onBack = onBack,
        modifier = modifier,
    ) {
        Text(
            text = "Pick a theme — every card stays multi-currency.",
            color = palette.mutedForeground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        SharedHubCardFace(
            model = previewModel,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Color",
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            items(ThemePickerOrder, key = { it.name }) { themeId ->
                ThemeSwatchCell(
                    themeId = themeId,
                    selected = themeId == selectedThemeId,
                    onClick = { onThemeSelected(themeId) },
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Pattern",
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            items(PatternPickerOrder, key = { it.name }) { pattern ->
                PatternChipCell(
                    label = pattern.displayLabel(),
                    selected = pattern == selectedPattern,
                    onClick = { onPatternSelected(pattern) },
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(palette.brand)
                .clickable(onClick = onConfirm),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Add card",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ThemeSwatchCell(
    themeId: HubCardThemeId,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val label = themeId.name
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) palette.foreground else palette.border,
                    shape = RoundedCornerShape(16.dp),
                )
                .background(hubCardThemeSwatchBrush(themeId))
                .clickable(onClick = onClick),
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(palette.foreground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = palette.cardSurface,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = palette.foreground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PatternChipCell(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) palette.foreground else palette.mutedSurface)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) palette.foreground else palette.border,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) palette.cardSurface else palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
